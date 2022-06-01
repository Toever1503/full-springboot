package com.models;

import com.entities.UserEntity;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

    private Long id;
    @Email
    @NotNull
    private String email;
    private String fullName;
    private String password;
    private Date birthDate;
    private Long mainAddress;
    private List<AddressModel> myAddress = Collections.emptyList();

    public static UserEntity toEntity(UserModel model) {
        if (model == null) throw new RuntimeException("UserModel is null");
        return UserEntity.builder()
                .fullName(model.getFullName())
                .email(model.getEmail())
                .password(model.getPassword())
                .birthDate(model.getBirthDate())
                .mainAddress(model.getMainAddress())
                .id(model.getId()).build();
    }

}
