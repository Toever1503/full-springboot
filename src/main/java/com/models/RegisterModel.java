package com.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterModel {
    @ApiModelProperty(notes = "User name", dataType = "String", example = "admin")
    @NotNull
    @NotBlank
    private String userName;
    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String url;
}
