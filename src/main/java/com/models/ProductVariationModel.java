package com.models;

import com.entities.ProductEntity;
import com.entities.ProductVariationEntity;
import com.entities.ProductVariationValueEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariationModel {
    private Long variationId;
    @NotNull
    @NotBlank
    private String variationName;

    private List<ProductVariationValueModel> values;

    public static ProductVariationEntity toEntity(ProductVariationModel model, ProductEntity product) {
        if (model == null) throw new RuntimeException("Variation model is null");
        ProductVariationEntity entity = ProductVariationEntity.builder()
                .id(model.variationId)
                .variationName(model.variationName)
                .product(product)
                .build();
        if (model.values != null)
            if (!model.values.isEmpty())
                entity.setVariationValues(model.values.stream().map(value -> ProductVariationValueModel.toEntity(value, entity)).collect(Collectors.toList()));
        return entity;
    }
}
