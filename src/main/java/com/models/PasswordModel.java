package com.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordModel {
    private String newPassword;
    private String token;
}
