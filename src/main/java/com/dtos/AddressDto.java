package com.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddressDto {
    private Long id;

    private Integer provineId;

    private Integer districtId;

    private Integer wardId;

    private String street;
}
