package com.models;

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
    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @Email
    private String email;
}
