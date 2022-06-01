package com.models;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordModel {
    @NotNull
    @NotBlank
    private String newPassword;

    @NotNull
    @NotBlank
    private String token;
}
