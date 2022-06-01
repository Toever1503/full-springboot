package com.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WardDto {
    private Integer id;

    private String name;

    private String prefix;
}
