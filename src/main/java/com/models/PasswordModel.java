package com.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordModel {

    @ApiModelProperty(notes = "User new password", dataType = "String", example = "147258369a")
    @NotNull
    @NotBlank
    private String newPassword;
    @ApiModelProperty(notes = "Identity Token", dataType = "JWToken", example = "eyasdasdasd...")
    @NotNull
    @NotBlank
    private String token;
}
