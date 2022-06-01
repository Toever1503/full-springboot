package com.dtos;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProvinceDto {
    private Integer id;

    private String name;

    private String code;
}
