package com.dtos;

import com.entities.ProductVariationValueEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductVariationValueDto {
    private Long variationValueId;
    private String value;
    private Boolean ísChecked;

    public static ProductVariationValueDto toDto(ProductVariationValueEntity entity) {
        if (entity == null) return null;
        return ProductVariationValueDto.builder()
                .variationValueId(entity.getId())
                .value(entity.getValue())
                .ísChecked(false)
                .build();
    }
}
