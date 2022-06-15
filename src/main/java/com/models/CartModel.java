package com.models;

import com.entities.CartEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CartModel {
    @ApiModelProperty(value = "Id of cart", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(value = "Id of option", dataType = "Long", example = "1")
    @NotNull
    private Long optionId;

    @ApiModelProperty(value = "Quantity of product", dataType = "Integer", example = "1")
    @NotNull
    @Min(1)
    private Integer quantity;

    @ApiModelProperty(value = "Id of product", dataType = "Long", example = "1")
    @NotNull
    private Long productId;

    public static CartEntity toEntity(CartModel model) {
        if(model == null) return null;
        return CartEntity.builder()
                .id(model.getId())
                .quantity(model.getQuantity())
                .build();
    }
}
