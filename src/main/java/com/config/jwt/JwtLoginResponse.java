package com.config.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtLoginResponse {
    private String token;
    private String type;
    private String username;
    private String email;
    private Long timeValid;
    private List<String> authorities;
    private String avatar;
}
