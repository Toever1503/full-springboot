package com.models;

import com.entities.OptionEntity;
import com.entities.ProductEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionModel {
    @ApiModelProperty(notes = "Option ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Option product", dataType = "String", example = "color")
    @NotNull
    @NotBlank
    private String optionName;
    @ApiModelProperty(notes = "quantity product", dataType = "Integer", example = "1")
    private Integer quantity;
    @ApiModelProperty(notes = "new price product", dataType = "Double", example = "1")
    private Double newPrice;
    @ApiModelProperty(notes = "new price product", dataType = "Double", example = "1")
    @NotNull
    private Double oldPrice;

    public static OptionEntity toEntity(OptionModel model, Long p) {
        if(model == null) new RuntimeException("ProductModel is null");
        return OptionEntity.builder()
                .id(model.getId())
                .optionName(model.getOptionName())
                .quantity(model.getQuantity())
                .newPrice(model.getNewPrice())
                .oldPrice(model.getOldPrice())
                .productId(p)
                .build();
    }
}
