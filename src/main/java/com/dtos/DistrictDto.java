package com.dtos;

import com.entities.Province;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DistrictDto {
    private Integer id;

    private String name;

    private String prefix;

}
