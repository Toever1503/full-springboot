package com.models;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(notes = "Address ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "Province Address", dataType = "Integer", example = "3")
    @NotNull
    @NotNull
    private Integer provinceId;

    @ApiModelProperty(notes = "District Address", dataType = "Integer", example = "1")
    @NotNull
    private Integer districtId;
    @ApiModelProperty(notes = "Ward Address", dataType = "Integer", example = "2")
    @NotNull
    private Integer wardId;
    @ApiModelProperty(notes = "Street Address", dataType = "String", example = "70st")
    @NotNull
    @NotBlank
    private String street;
}
