package com.services;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.dtos.AddressDto;
import com.entities.Address;
import com.entities.UserEntity;
import com.models.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

public interface IUserService extends IBaseService<UserEntity, UserModel, Long>{
    boolean signUp(RegisterModel registerModel);

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean forgetPassword( ForgetPasswordModel model);

    boolean setPassword(PasswordModel model);
    boolean changePassword(ChangePasswordModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    UserEntity getMyProfile();

    Set<Address> getMyAddresses();

    boolean deleteMyAddress(Long id);

    boolean setMainAddress(Long id);

    Address addMyAddress(AddressModel model);

    Address updateMyAddress(AddressModel model);

    UserEntity updateUserProfile(UserProfileModel model);
}
