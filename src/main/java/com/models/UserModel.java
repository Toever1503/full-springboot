package com.models;

import com.entities.UserEntity;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String password;
    private Date birthDate;
    private Long mainAddress;

    public static UserEntity toEntity(UserModel model){
        if(model==null){
            return null;
        }
        else {
            return UserEntity.builder()
                    .userName(model.getUserName())
                    .fullName(model.getFullName())
                    .email(model.getEmail())
                    .password(model.getPassword())
                    .birthDate(model.getBirthDate())
                    .main_address(model.getMainAddress())
                    .id(model.getId()).build();
        }
    }

}
