package com.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordModel {
    private String userName;
    private String url;
}
