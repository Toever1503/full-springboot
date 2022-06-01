package com.models;

import lombok.*;

import javax.persistence.Entity;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterModel {
    private String userName;
    private String email;
}
