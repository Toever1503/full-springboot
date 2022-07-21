package com.webs;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.dtos.AddressDto;
import com.dtos.ResponseDto;
import com.dtos.UserDto;
import com.models.*;
import com.models.filters.UserFilterModel;
import com.models.specifications.UserSpecification;
import com.services.IUserService;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@Validated
public class UserResources {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final IUserService userService;

    public UserResources(IUserService userService) {
        this.userService = userService;
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping
    public ResponseDto adminGetAllUser(Pageable page) {
        log.info("{} is getting all users ", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.findAll(page).map(UserDto::toDto), "Get all users ");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("{id}")
    public ResponseDto getUser(@PathVariable("id") Long id) {
        log.info("{} is getting detail user id: {}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(UserDto.toDto(this.userService.findById(id)), "Get user id: " + id);
    }

    @Transactional
    @PutMapping("my-profile/update")
    public ResponseDto updateMyProfile(@Valid UserProfileModel model) {
        log.info("{} is updating my profile", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(UserDto.toDto(this.userService.updateUserProfile(model)), "Update my profile");
    }

    @Transactional
    @GetMapping("my-profile")
    public ResponseDto getMyProfile() {
        log.info("{} is getting their profile", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(UserDto.toDto(this.userService.getMyProfile()), "Get my profile");
    }

    @Transactional
    @GetMapping("my-addresses")
    public ResponseDto getMyAddresses() {
        log.info("{} is getting their addresses", SecurityUtils.getCurrentUser().getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("addresses", this.userService.getMyAddresses().stream().map(AddressDto::toDto).collect(Collectors.toList()));
        map.put("mainAddress", SecurityUtils.getCurrentUser().getUser().getMainAddress());
        return ResponseDto.of(map, "Get all my addresses");
    }

    @Transactional
    @PostMapping("my-addresses") // create address
    public ResponseDto addMyAddress(@Valid @RequestBody AddressModel model) {
        log.info("{} is adding new address", SecurityUtils.getCurrentUser().getUsername());
        model.setId(null);
        return ResponseDto.of(AddressDto.toDto(this.userService.addMyAddress(model)), "Add new address");
    }

    @Transactional
    @PutMapping("my-addresses/{id}") // update address
    public ResponseDto updateMyAddress(@PathVariable("id") Long id, @Valid @RequestBody AddressModel model) {
        model.setId(id);
        log.info("{} is updating address id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(AddressDto.toDto(this.userService.updateMyAddress(model)), "Update address ID: " + id);
    }

    @Transactional
    @PatchMapping("my-addresses/main/{id}") // set main address
    public ResponseDto setMainAddress(@PathVariable("id") Long id) {
        log.info("{} is setting main address", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.setMainAddress(id), "Set main address");
    }

    @Transactional
    @DeleteMapping("my-addresses/{id}")
    public ResponseDto deleteMyAddress(@PathVariable("id") Long id) {
        log.info("{} is deleting their address id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        if (this.userService.deleteMyAddress(id))
            return ResponseDto.of(true, "Delete my address");
        else
            return ResponseDto.of(null, "Delete address");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @DeleteMapping("delete-user/{id}")
    public ResponseDto deleteUser(@PathVariable("id") Long id) {
        log.info("{} is deleting user id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(this.userService.deleteById(id), "Account don't login, account is deleted");
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseDto signUpUser(@RequestBody @Valid RegisterModel model) {
        log.info("{Anonymous} is signing up");
        return ResponseDto.of(userService.signUp(model), "Signup");
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        log.info("{} is logging in system", model.getUsername());
        JwtLoginResponse jwtUserLoginModel = userService.logIn(model);
        return jwtUserLoginModel == null ? new ResponseEntity<>(ResponseDto.of(null, "Sai mật khẩu, Login"), HttpStatus.BAD_REQUEST) : new ResponseEntity<>(ResponseDto.of(jwtUserLoginModel, "login"), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/forget-password")
    public ResponseDto forgetPassword(@RequestBody @Valid ForgetPasswordModel model) {
        log.info("{} is requesting forget password", model.getUserName());
        userService.forgetPassword(model);
        return ResponseDto.of(model.getUserName(), "Please check your reset password email that we have sent");
    }

    @Transactional
    @PostMapping(value = "/set-password")
    public ResponseDto setPassword(@RequestBody @Valid PasswordModel model) {
        log.info("user {} is setting new password");
        return ResponseDto.of(userService.setPassword(model) ? true : null, "Set password");
    }

    @Transactional
    @PostMapping(value = "/change-password")
    public ResponseDto changePassword(@RequestBody @Valid ChangePasswordModel model) {
        log.info("user {} is changing password", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(userService.changePassword(model) ? true : null, "Password change");
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filterUser(@RequestBody UserFilterModel model, Pageable page) {
        log.info("{} is filtering user", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(userService.filter(page, Specification.where(UserSpecification.filter(model))).map(UserDto::toDto), "Filter user");
    }

    @Transactional
    @GetMapping("get-my-avatar")
    public ResponseDto getMyAvatar() {
        log.info("{} is getting their avatar", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(SecurityUtils.getCurrentUser().getUser().getAvatar(), "Get my avatar");
    }

    @Transactional
    @PutMapping("update-profile/avatar")
    public ResponseDto updateAvatar(@RequestPart MultipartFile avatar) {
        log.info("{} is updating their avatar", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(userService.updateAvatar(avatar), "Update avatar");
    }

    @Transactional
    @PatchMapping("change-status/{id}")
    public ResponseDto changeStatusUser(@PathVariable Long id) {
        log.info("admin {} is changing status user", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.changeStatus(id), "Change user's status");
    }

    @Transactional
    @PatchMapping("change-lock-status/{id}")
    public ResponseDto changeLockStatusUser(@PathVariable Long id) {
        log.info("admin {} is changing lock status user", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.userService.changeLockStatus(id), "Change user's lock status");
    }
}
