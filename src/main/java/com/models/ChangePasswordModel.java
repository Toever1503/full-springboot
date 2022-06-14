package com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordModel {
    @NotNull
    @NotBlank
    @Min(4)
    private String oldPassword;
    @NotNull
    @NotBlank
    @Min(4)
    private String newPassword;
}
