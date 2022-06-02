package com.services;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.entities.UserEntity;
import com.models.ForgetPasswordModel;
import com.models.PasswordModel;
import com.models.RegisterModel;
import com.models.UserModel;

import javax.servlet.http.HttpServletRequest;

public interface IUserService extends IBaseService<UserEntity, UserModel, Long>{
    boolean signUp(RegisterModel registerModel);

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean forgetPassword( ForgetPasswordModel model);

    boolean changePassword(PasswordModel model);

    boolean tokenFilter(String substring, HttpServletRequest req);

    UserEntity getMyProfile();
}
