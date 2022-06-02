package com.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordModel {
    @ApiModelProperty(notes = "User name", dataType = "String", example = "user2")
    @NotNull
    @NotBlank
    private String userName;

    @ApiModelProperty(notes = "Forget password link", dataType = "String", example = "http://ijustforgotmypass.com")
    @NotNull
    @NotBlank
    private String url;
}
