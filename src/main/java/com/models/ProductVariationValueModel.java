package com.models;


import com.entities.ProductVariationEntity;
import com.entities.ProductVariationValueEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariationValueModel {
    private Long variationValueId;
    private String value;

    public static ProductVariationValueEntity toEntity(ProductVariationValueModel model, ProductVariationEntity entity) {
        if (model == null) throw new RuntimeException("Variation value model is null");
        return ProductVariationValueEntity.builder()
                .id(model.variationValueId)
                .value(model.value)
                .variation(entity)
                .build();
    }
}
