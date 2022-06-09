package com.models;

import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaModel {
    private Long id;
    private String metaKey;
    private String metaValue;

    public static ProductMetaEntity toEntity(ProductMetaModel model, Long product) {
        if(model == null) new RuntimeException("ProductModel is null");
        return ProductMetaEntity.builder()
                .id(model.getId())
                .metaKey(model.getMetaKey())
                .metaValue(model.getMetaValue())
                .productId(product)
                .build();
    }
}
