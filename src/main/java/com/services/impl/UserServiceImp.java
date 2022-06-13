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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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
        logger.info("{%s} finding user id: {%d}", SecurityUtils.getCurrentUsername(), id);
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
        logger.info("{%s} is updating userid: {%d}", SecurityUtils.getCurrentUsername(), model.getId());
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
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
   @Transactional(rollbackFor = RuntimeException.class)
    public boolean signUp(RegisterModel registerModel) {
        if (!userRepository.findUserEntityByUserNameOrEmail(registerModel.getUserName(), registerModel.getEmail()).isPresent()) {
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
        throw new RuntimeException("User already existed!");
    }

    @Override
    public JwtLoginResponse logIn(JwtUserLoginModel userLogin) {
        UserDetails userDetail = new CustomUserDetail(this.findByUsername(userLogin.getUsername()));
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetail, userLogin.getPassword(), userDetail.getAuthorities()));
        long timeValid = userLogin.isRemember() ? 86400 * 7 : 1800l;
        return JwtLoginResponse.builder()
                .token(this.jwtProvider.generateToken(userDetail.getUsername(), timeValid))
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
    public boolean changePassword(PasswordModel model) {
        String[] userToken = jwtProvider.getUsernameFromToken(model.getToken()).split("-");
        UserEntity user = this.findByUsername(userToken[0]);
        if (!user.getCode().equals(userToken[1])) throw new RuntimeException("User code mismatch!");
        user.setPassword(passwordEncoder.encode(model.getNewPassword()));
        user.setCode("0");
        user.setStatus(true);
        userRepository.save(user);
        return true;
    }

    // Token filter, check token is valid and set to context
   @Transactional(rollbackFor = RuntimeException.class)
    public boolean tokenFilter(String token, HttpServletRequest req) {
        String username = this.jwtProvider.getUsernameFromToken(token);
        CustomUserDetail userDetail = new CustomUserDetail(this.findByUsername(username));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return true;
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
        if(deleteAddressByID)
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
        if(this.addressService.findById(id).getUser().getId() == user.getId()|| SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)){
            user.setMainAddress(id);
            this.userRepository.save(user);
            return true;
        }else
            throw new RuntimeException("Address not found!, id: " + id);
//        if (!addressService.findByUid(user.getId()).stream().anyMatch(a -> a.getId() == id))
//            throw new RuntimeException("Address not found!, id: " + id);
    }

    @Override
    public Address addMyAddress(AddressModel model) {
        UserEntity user = this.findById(SecurityUtils.getCurrentUserId());
        Address address = this.addressService.add(model);
        return address;
    }

    @Override
    public Address updateMyAddress(AddressModel model) {
        // id 2 want edit address, check
        // usercreated id = 1  == 2
        Address oriAddress = this.addressService.findById(model.getId());
        if(oriAddress.getUser().getId() == SecurityUtils.getCurrentUserId()|| SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)){
            return this.addressService.update(model);
        }
        else
            return null;
//        UserEntity user = this.findById(SecurityUtils.getCurrentUserId());
//        if (!addressService.findByUid(SecurityUtils.getCurrentUserId()).stream().anyMatch(a -> a.getId() == model.getId())|| SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR))
//            throw new RuntimeException("Address not found!, id: " + model.getId());

    }

    @Override
    public UserEntity updateUserProfile(UserProfileModel model) {
        UserEntity userEntity = this.findById(SecurityUtils.getCurrentUserId());
        userEntity.setFullName(model.getFullName());
        userEntity.setPhone(model.getPhone());
        userEntity.setBirthDate(model.getBirthDate());
        userEntity.setSex(model.getSex());

        UserEntity checkUser = this.userRepository.findByPhone(model.getPhone());

        if(checkUser != null){
            if(checkUser.getId() != userEntity.getId()){
                throw new RuntimeException("Phone number already exists!");
            }
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
}
