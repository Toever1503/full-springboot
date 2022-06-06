package com.services;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.entities.Address;
import com.entities.UserEntity;
import com.models.ForgetPasswordModel;
import com.models.PasswordModel;
import com.models.RegisterModel;
import com.models.UserModel;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface IUserService extends IBaseService<UserEntity, UserModel, Long>{
    boolean signUp(RegisterModel registerModel);

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean forgetPassword( ForgetPasswordModel model);

    boolean changePassword(PasswordModel model);

    boolean tokenFilter(String substring, HttpServletRequest req);

    UserEntity getMyProfile();

    Set<Address> getMyAddresses();

    boolean deleteMyAddress(Long id);
}
