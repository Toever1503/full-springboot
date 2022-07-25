package com.services;

import com.config.jwt.JwtLoginResponse;
import com.config.jwt.JwtUserLoginModel;
import com.entities.AddressEntity;
import com.entities.UserEntity;
import com.models.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public interface IUserService extends IBaseService<UserEntity, UserModel, Long>{
    boolean signUp(RegisterModel registerModel);

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean forgetPassword( ForgetPasswordModel model);

    boolean setPassword(PasswordModel model);
    boolean changePassword(ChangePasswordModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    UserEntity getMyProfile();

    Set<AddressEntity> getMyAddresses();

    boolean deleteMyAddress(Long id);

    boolean setMainAddress(Long id);

    AddressEntity addMyAddress(AddressModel model);

    AddressEntity updateMyAddress(AddressModel model);

    UserEntity updateUserProfile(UserProfileModel model);

    boolean updateAvatar(MultipartFile avatar);

    boolean changeStatus(Long userId);

    boolean changeLockStatus(Long userId);

    String updateAvatar1(MultipartFile avatar);
}
