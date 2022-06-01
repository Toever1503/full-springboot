package com.models;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordModel {
    @NotNull
    @NotBlank
    private String userName;
    @NotNull
    @NotBlank
    private String url;
}
