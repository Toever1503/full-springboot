package com.models;

import com.entities.UserEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @ApiModelProperty(notes = "User ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    @Email
    @NotNull
    @Length(min = 10,max = 255)
    private String email;

    @ApiModelProperty(notes = "User full name", dataType = "String", example = "Nguyen Van A")
    @Length(min = 10,max = 255)
    private String fullName;

    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    @Length(min = 6,max = 255)
    private String password;

    @ApiModelProperty(notes = "User birthdate", dataType = "Date", example = "19/09/2009")
    private Date birthDate;

    private List<Long> roles;

    public static UserEntity toEntity(UserModel model) {
        if (model == null) throw new RuntimeException("UserModel is null");
        return UserEntity.builder()
                .fullName(model.getFullName())
                .email(model.getEmail())
                .password(model.getPassword())
                .birthDate(model.getBirthDate())
                .id(model.getId()).build();
    }

}
