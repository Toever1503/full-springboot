package com.dtos;

import com.entities.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductSkuDto {
    private Long id;
    private Double price;
    private Double oldPrice; // similar with old price and new price. range from 1-100 percent.
    private String image;
    private String skuCode;
    private Integer inventoryQuantity;
    private String productName;
    private Boolean isValid;

    private String optionName;

    public static ProductSkuDto toDto(ProductSkuEntity entity) {
        if (entity == null) return null;
        return ProductSkuDto.builder()
                .id(entity.getId())
                .price(entity.getPrice())
                .skuCode(entity.getSkuCode())
                .oldPrice(entity.getOldPrice())
                .image(entity.getImage())
                .inventoryQuantity(entity.getInventoryQuantity())
                .isValid(entity.getIsValid())
                .optionName(entity.getOptionName())
                .productName(entity.getProduct().getName())
                .build();
    }
}
