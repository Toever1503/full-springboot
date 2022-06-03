package com.services.impl;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtProvider;
import com.config.jwt.JwtUserLoginModel;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.ForgetPasswordModel;
import com.models.PasswordModel;
import com.models.RegisterModel;
import com.models.UserModel;
import com.repositories.IRoleRepository;
import com.repositories.IUserRepository;
import com.services.CustomUserDetail;
import com.services.IAddressService;
import com.services.IUserService;
import com.services.MailService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Order(5)
public class UserServiceImp implements IUserService {
    final
    IUserRepository userRepository;
    final
    IRoleRepository roleRepository;
    final
    JwtProvider jwtProvider;
    final
    AuthenticationManager authenticationManager;
    final
    PasswordEncoder passwordEncoder;

    final
    MailService mailService;

    final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IAddressService addressService;

    public UserServiceImp(IUserRepository userRepository,
                          IRoleRepository roleRepository,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          MailService mailService,
                          IAddressService addressService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.addressService = addressService;
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
        return null;
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

        if (!model.getMyAddress().isEmpty()) {
            if (model.getMainAddress() == null) throw new RuntimeException("mainAddress must not be null");
            u.setMyAddress(this.addressService.add(model.getMyAddress()).stream().collect(Collectors.toSet()));
        }

        if (model.getPassword() != null)
            u.setPassword(passwordEncoder.encode(model.getPassword()));
        u.setMainAddress(model.getMainAddress());
        return userRepository.save(u);
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
    @Transactional
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
                .timeValid(timeValid).build();
    }

    public UserEntity findByUsername(String userName) {
        return userRepository.findUserEntityByUserNameOrEmail(userName, userName).orElseThrow(() -> new RuntimeException("Not found username: " + userName));
    }

    public String codeGenerator() {
        return String.valueOf(random.nextInt(899999) + 100000);
    }

    @Override
    @Transactional
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
    @Transactional
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
}
