package com.models;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddressModel {
    private Long id;

    @NotNull
    private Integer provinceId;

    @NotNull
    private Integer districtId;

    @NotNull
    private Integer wardId;

    @NotNull
    @NotBlank
    private String street;
}
