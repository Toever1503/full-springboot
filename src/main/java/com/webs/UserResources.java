package com.webs;

import com.config.jwt.JwtUserLoginModel;
import com.dtos.AddressDto;
import com.dtos.ResponseDto;
import com.dtos.UserDto;
import com.models.*;
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
    public ResponseDto updateMyProfile(@Valid UserProfileModel model) {
        log.info("{%s} is updating my profile", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(UserDto.toDto(this.userService.updateUserProfile(model)), "Update my profile successfully");
    }
    @Transactional
    @GetMapping("my-profile")
    public ResponseDto getMyProfile() {
        log.info("{%s} is getting their profile", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(UserDto.toDto(this.userService.getMyProfile()), "Get my profile");
    }

    @Transactional
    @GetMapping("my-addresses")
    public ResponseDto getMyAddresses() {
        log.info("{%s} is getting their addresses", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.getMyAddresses().stream().map(AddressDto::toDto).collect(Collectors.toList()), "Get all my addresses successfully");
    }

    @Transactional
    @PostMapping("my-addresses") // create address
    public ResponseDto addMyAddress(@Valid @RequestBody AddressModel model) {
        log.info("{%s} is adding new address", SecurityUtils.getCurrentUser().getUsername());
        model.setId(null);
        return ResponseDto.of(AddressDto.toDto(this.userService.addMyAddress(model)), "Add new address successfully!");
    }

    @Transactional
    @PutMapping("my-addresses/{id}") // update address
    public ResponseDto updateMyAddress(@PathVariable("id") Long id, @Valid @RequestBody AddressModel model) {
        model.setId(id);
        log.info("{%s} is updating address id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(AddressDto.toDto(this.userService.updateMyAddress(model)), "Update address successfully!, ID: " + id);
    }

    @Transactional
    @PatchMapping("my-addresses/main/{id}") // set main address
    public ResponseDto setMainAddress(@PathVariable("id") Long id) {
        log.info("{%s} is setting main address", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.setMainAddress(id), "Set main address successfully");
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
        log.info("{user} is changing password");
        return ResponseDto.of(userService.changePassword(model) ? true : null, "Password change successfully");
    }
}
