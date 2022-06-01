package com.webs;

import com.config.jwt.JwtUserLoginModel;
import com.dtos.ResponseDto;
import com.models.ForgetPasswordModel;
import com.models.PasswordModel;
import com.models.RegisterModel;
import com.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("users")
public class UserResources {
    @Autowired
    IUserService userService;


    @Transactional
    @PostMapping("/signup")
    public ResponseDto signUpUser(@RequestBody @Valid RegisterModel model) {
        return ResponseDto.of(userService.signUp(model), "Signup ");
    }

    @Transactional
    @PostMapping("/login")
    public ResponseDto loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        return ResponseDto.of(userService.logIn(model), "Login Success");
    }

    @Transactional
    @PostMapping("/forget-password")
    public ResponseDto forgetPassword(@RequestBody @Valid ForgetPasswordModel model) {
        userService.forgetPassword(model);
        return ResponseDto.of(model.getUserName(), "Please check your reset password email that we have sent");
    }

    @Transactional
    @PostMapping(value = "/change-password")

    public ResponseDto changePassword(@RequestBody PasswordModel model) {
        return ResponseDto.of(userService.changePassword(model) ? true : null, "Password change successfully");
    }
}
