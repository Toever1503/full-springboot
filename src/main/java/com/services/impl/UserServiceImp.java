package com.services.impl;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtProvider;
import com.config.jwt.JwtUserLoginModel;
import com.entities.Address;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.*;
import com.repositories.IRoleRepository;
import com.repositories.IUserRepository;
import com.services.CustomUserDetail;
import com.services.IAddressService;
import com.services.IUserService;
import com.services.MailService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Order(5)
public class UserServiceImp implements IUserService {
    private final
    IUserRepository userRepository;
    private final
    IRoleRepository roleRepository;
    private final
    JwtProvider jwtProvider;
    private final
    AuthenticationManager authenticationManager;
    private final
    PasswordEncoder passwordEncoder;

    private final
    MailService mailService;

    private final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IAddressService addressService;
    private final FileUploadProvider fileUploadProvider;

    public UserServiceImp(IUserRepository userRepository,
                          IRoleRepository roleRepository,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          MailService mailService,
                          IAddressService addressService, FileUploadProvider fileUploadProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.addressService = addressService;
        this.fileUploadProvider = fileUploadProvider;
        try {
            this.roleRepository.save(RoleEntity.builder().roleId(1L).roleName(RoleEntity.USER).build());
            this.roleRepository.save(RoleEntity.builder().roleId(2L).roleName(RoleEntity.ADMINISTRATOR).build());
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<UserEntity> findAll(Pageable page) {
        return userRepository.findAll(page);
    }

    @Override
    public Page<UserEntity> filter(Pageable page, Specification<UserEntity> specs) {
        return this.userRepository.findAll(specs, page);
    }

    @Override
    public UserEntity findById(Long id) {
        logger.info("{} finding user id: {%d}", SecurityUtils.getCurrentUsername(), id);
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found user id: " + id));
    }

    @Override
    public UserEntity add(UserModel model) {
        return null;
    }

    @Override
    public List<UserEntity> add(List<UserModel> model) {
        return null;
    }

    @Override
    public UserEntity update(UserModel model) {
        logger.info("{} is updating userid: {%d}", SecurityUtils.getCurrentUsername(), model.getId());
        UserEntity u = this.findById(model.getId());
        u.setBirthDate(model.getBirthDate());
        u.setFullName(model.getFullName());
        u.setPhone(u.getPhone());
        if (model.getPassword() != null)
            u.setPassword(passwordEncoder.encode(model.getPassword()));
        this.setRoles(u, model.getRoles());
        return userRepository.save(u);
    }

    public void setRoles(UserEntity user, List<Long> roles) {
        if (roles == null || roles.isEmpty())
            user.setRoleEntity(Collections.singleton(this.roleRepository.findRoleEntityByRoleName(RoleEntity.USER)));
        else
            user.setRoleEntity(this.roleRepository.findAllByRoleIdIn(roles));
    }

    @Override
    public boolean deleteById(Long id) {
        UserEntity userEntity = this.findById(id);
        userEntity.setStatus(false);
        this.userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean signUp(RegisterModel registerModel) {
        if (this.userRepository.findByUserName(registerModel.getUserName()) != null) {
            throw new RuntimeException("Username has already registered!");
        } else if (this.userRepository.findByEmail(registerModel.getUserName()) != null)
            throw new RuntimeException("Email has already registered!");

        Set<RoleEntity> roleEntitySet = new HashSet<>();
        roleEntitySet.add(roleRepository.findRoleEntityByRoleName(RoleEntity.USER));
        UserEntity user = userRepository.save(UserEntity.builder().userName(registerModel.getUserName()).email(registerModel.getEmail()).code(codeGenerator()).roleEntity(roleEntitySet).status(false).build());
        new Thread("Sign Up Mail Sender") {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append(registerModel.getUserName()).append("-").append(user.getCode());
                String urlResponse = registerModel.getUrl() + jwtProvider.generateToken(sb.toString(), 86400l);
                Map<String, Object> context = new HashMap<>();
                context.put("url", urlResponse);
                try {
                    mailService.sendMail("VerificationMailTemplate.html", registerModel.getEmail(), "Active your account", context);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        return true;
    }

    @Override
    public JwtLoginResponse logIn(JwtUserLoginModel userLogin) {
        UserEntity user = this.findByUsername(userLogin.getUsername());
        if (user.isLockStatus())
            throw new RuntimeException("User has locked!");
        else if (!user.isStatus())
            throw new RuntimeException("User hasn't active!");
        UserDetails userDetail = new CustomUserDetail(user);
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetail, userLogin.getPassword(), userDetail.getAuthorities()));
        long timeValid = userLogin.isRemember() ? 86400 * 7 : 1800l;
        return JwtLoginResponse.builder()
                .token(this.jwtProvider.generateToken(userDetail.getUsername(), timeValid))
                .email(findByUsername(userDetail.getUsername()).getEmail())
                .username(findByUsername(userDetail.getUsername()).getUserName())
                .avatar(findByUsername(userDetail.getUsername()).getAvatar())
                .type("Bearer").authorities(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .timeValid(timeValid)
                .avatar(findByUsername(userDetail.getUsername()).getAvatar())
                .build();
    }

    public UserEntity findByUsername(String userName) {
        return userRepository.findUserEntityByUserNameOrEmail(userName, userName).orElseThrow(() -> new RuntimeException("Not found username: " + userName));
    }

    public String codeGenerator() {
        return String.valueOf(random.nextInt(899999) + 100000);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean forgetPassword(ForgetPasswordModel model) {
        UserEntity user = this.findByUsername(model.getUserName());
        user.setCode(codeGenerator());
        new Thread("Forget Password Mail Sender") {
            @Override
            public void run() {
                String userToken = user.getUserName().concat("-").concat(user.getCode());
                System.out.println("raw  userToken: " + userToken);
                userToken = model.getUrl() + jwtProvider.generateToken(userToken, 86400);
                System.out.println("after generate token: " + userToken);
                Map<String, Object> context = new HashMap<>();
                context.put("url", userToken);
                try {
                    mailService.sendMail("VerificationMailTemplate.html", user.getEmail(), "Reset Password", context);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        return true;

    }

    @Override
    public boolean setPassword(PasswordModel model) {
        String[] userToken = jwtProvider.getUsernameFromToken(model.getToken()).split("-");
        UserEntity user = this.findByUsername(userToken[0]);
        if (!user.getCode().equals(userToken[1])) throw new RuntimeException("User code mismatch!");
        user.setPassword(passwordEncoder.encode(model.getNewPassword()));
        user.setCode("0");
        user.setStatus(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordModel model) {
        UserEntity user = SecurityUtils.getCurrentUser().getUser();
        if (!BCrypt.checkpw(model.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password mismatch!");
        }
        user.setPassword(passwordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    // Token filter, check token is valid and set to context
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean tokenFilter(String token, HttpServletRequest req, HttpServletResponse res) {
        try {
            String username = this.jwtProvider.getUsernameFromToken(token);
            CustomUserDetail userDetail = new CustomUserDetail(this.findByUsername(username));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserEntity getMyProfile() {
        return this.findById(SecurityUtils.getCurrentUserId());
    }

    @Override
    public Set<Address> getMyAddresses() {
        return this.addressService.findByUid(SecurityUtils.getCurrentUserId());
    }

    @Override
    public boolean deleteMyAddress(Long id) {
//        UserEntity user = this.findById(SecurityUtils.getCurrentUserId());
//        if(this.addressService.findByUid(user.getId())!=null){
//            Set<Address> myAddress = this.addressService.findByUid(user.getId());
//            myAddress.stream().filter(address -> address.getId()!=id).collect(Collectors.toSet());
//            myAddress.stream().forEach(address -> this.addressService.update(address));
        boolean deleteAddressByID = addressService.deleteById(id);
        if (deleteAddressByID)
            return true;
        else
            return false;

//        if(user.getMyAddress().contains(addressService.findById(id))){
//            if(user.getMainAddress() == id){
//                user.setMainAddress(null);
//            }
//            user.setMyAddress(user.getMyAddress().stream().filter(a -> a.getId() != id).collect(Collectors.toSet()));
//            this.userRepository.save(user);
//            this.addressService.deleteById(id);
//            return true;
//        }else if(SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)){
////            List<UserEntity> userEntity = userRepository.findAllByMyAddressContaining(addressService.findById(id));
////            List<UserEntity> userEntitiesMainAddr = userEntity.stream().filter(userEntity1 -> userEntity1.getMainAddress()==id).collect(Collectors.toList());
////            userEntitiesMainAddr.stream().forEach(userEntity1 -> userEntity1.setMainAddress(null));
////
////            userEntity.stream().forEach(userEntity1 -> userEntity1.setMyAddress(userEntity1.getMyAddress().stream().filter(b->b.getId()!=id).collect(Collectors.toSet())));
////            userEntity.stream().forEach(userEntity1 -> this.userRepository.save(userEntity1));
////            userEntity.stream().forEach(userEntity1 -> this.addressService.deleteById(id));
//            return true;
//        }
//        else
    }

    @Override
    public boolean setMainAddress(Long id) {
        UserEntity user = this.findById(SecurityUtils.getCurrentUserId());
        if (this.addressService.findById(id).getUser().getId() == user.getId() || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)) {
            user.setMainAddress(id);
            this.userRepository.save(user);
            return true;
        } else
            throw new RuntimeException("Address not found!, id: " + id);
//        if (!addressService.findByUid(user.getId()).stream().anyMatch(a -> a.getId() == id))
//            throw new RuntimeException("Address not found!, id: " + id);
    }

    @Override
    public Address addMyAddress(AddressModel model) {
        Address address = this.addressService.add(model);
        return address;
    }

    @Override
    public Address updateMyAddress(AddressModel model) {
        // id 2 want edit address, check
        // usercreated id = 1  == 2
        Address oriAddress = this.addressService.findById(model.getId());
        if (oriAddress.getUser().getId() == SecurityUtils.getCurrentUserId() || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)) {
            return this.addressService.update(model);
        } else
            return null;
//        UserEntity user = this.findById(SecurityUtils.getCurrentUserId());
//        if (!addressService.findByUid(SecurityUtils.getCurrentUserId()).stream().anyMatch(a -> a.getId() == model.getId())|| SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR))
//            throw new RuntimeException("Address not found!, id: " + model.getId());

    }

    @Override
    public UserEntity updateUserProfile(UserProfileModel model) {
        UserEntity userEntity = this.findById(SecurityUtils.getCurrentUserId());
        userEntity.setFullName(model.getFullName());

        userEntity.setBirthDate(model.getBirthDate());
        userEntity.setSex(model.getSex());

        if (model.getPhone() != null) {
            UserEntity checkUser = this.userRepository.findByPhone(model.getPhone()); // vudt
            if (checkUser != null) {
                if (checkUser.getId() != userEntity.getId()) {
                    throw new RuntimeException("Phone number already exists!");
                }
            }
            if (!model.getPhone().matches("(84|0[3|5|7|8|9])+([0-9]{8})\\\\b"))
                throw new RuntimeException("Phone number must be in format: 84xxxxxxxx!");
            userEntity.setPhone(model.getPhone());
        }


        if (model.getPassword() != null)
            userEntity.setPassword(passwordEncoder.encode(model.getPassword()));
        if (model.getAvatar() != null) {
            try {
                this.fileUploadProvider.deleteFile(userEntity.getAvatar());
                userEntity.setAvatar(this.fileUploadProvider.uploadFile(UserEntity.FOLDER + userEntity.getUserName() + "/", model.getAvatar()));
            } catch (IOException e) {
                throw new RuntimeException("File upload error!");
            }
        }
        return this.userRepository.save(userEntity);
    }

    @Override
    public boolean updateAvatar(MultipartFile avatar) {
        if (avatar.isEmpty())
            throw new RuntimeException("Avatar file is empty!");
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        if (userEntity.getAvatar() != null)
            this.fileUploadProvider.deleteFile(userEntity.getAvatar());
        try {
            userEntity.setAvatar(this.fileUploadProvider.uploadFile(UserEntity.FOLDER + userEntity.getUserName() + "/", avatar));
        } catch (IOException e) {
            throw new RuntimeException("File Avatar upload error!");
        }
        return this.userRepository.save(userEntity) != null;
    }

    @Override
    public boolean changeStatus(Long userId) {
        UserEntity user = this.findById(userId);
        user.setStatus(!user.isStatus());
        return this.userRepository.save(user) != null;
    }

    @Override
    public boolean changeLockStatus(Long userId) {
        UserEntity user = this.findById(userId);
        user.setLockStatus(!user.isLockStatus());
        return this.userRepository.save(user) != null;
    }
}
