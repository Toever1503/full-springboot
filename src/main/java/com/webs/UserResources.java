package com.webs;

import com.config.jwt.JwtUserLoginModel;
import com.dtos.AddressDto;
import com.dtos.ResponseDto;
import com.dtos.UserDto;
import com.models.ForgetPasswordModel;
import com.models.PasswordModel;
import com.models.RegisterModel;
import com.models.UserModel;
import com.services.IUserService;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserResources {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IUserService userService;


    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("{id}")
    public ResponseDto getUser(@PathVariable("id") Long id) {
        log.info("{%s} is getting detail user id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(UserDto.toDto(this.userService.findById(id)), "Get user id: " + id);
    }

    @Transactional
    @PutMapping("my-profile/update")
    public ResponseDto updateMyProfile(@Valid UserModel model) {
        log.info("{%s} is updating my profile", SecurityUtils.getCurrentUser().getUsername());
        model.setId(SecurityUtils.getCurrentUserId());
        model.setRoles(SecurityUtils.getCurrentUser().getUser().getRoleEntity().stream().map(roleEntity -> roleEntity.getRoleId()).collect(Collectors.toList()));
        return ResponseDto.of(UserDto.toDto(this.userService.update(model)), "Update my profile successfully");
    }

    @Transactional
    @GetMapping("my-addresses")
    public ResponseDto getMyAddresses() {
        log.info("{%s} is getting their addresses", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.getMyAddresses().stream().map(AddressDto::toDto).collect(Collectors.toList()), "Get all my addresses successfully");
    }


    @Transactional
    @GetMapping("my-profile")
    public ResponseDto getMyProfile() {
        log.info("{%s} is getting their profile", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(UserDto.toDto(this.userService.getMyProfile()), "Get my profile");
    }

    @Transactional
    @DeleteMapping("my-addresses/{id}")
    public ResponseDto deleteMyAddress(@PathVariable("id") Long id) {
        log.info("{%s} is deleting their address id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(this.userService.deleteMyAddress(id), "Delete my address successfully");
    }


    @Transactional
    @PostMapping("/signup")
    public ResponseDto signUpUser(@RequestBody @Valid RegisterModel model) {
        log.info("{Anonymous} is signing up");
        return ResponseDto.of(userService.signUp(model), "Signup ");
    }

    @Transactional
    @PostMapping("/login")
    public ResponseDto loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        log.info("{%s} is logging in system", model.getUsername());
        return ResponseDto.of(userService.logIn(model), "Login Success");
    }

    @Transactional
    @PostMapping("/forget-password")
    public ResponseDto forgetPassword(@RequestBody @Valid ForgetPasswordModel model) {
        log.info("{%s} is requesting forget password", model.getUserName());
        userService.forgetPassword(model);
        return ResponseDto.of(model.getUserName(), "Please check your reset password email that we have sent");
    }

    @Transactional
    @PostMapping(value = "/change-password")
    public ResponseDto changePassword(@RequestBody PasswordModel model) {
        log.info("{%s} is changing password", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(userService.changePassword(model) ? true : null, "Password change successfully");
    }
}
