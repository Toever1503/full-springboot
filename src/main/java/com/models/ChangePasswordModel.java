package com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordModel {
    @NotNull
    @NotBlank
    @Length(min = 4, max = 20)
    private String oldPassword;
    @NotNull
    @NotBlank
    @Length(min = 4, max = 20)
    private String newPassword;
}
