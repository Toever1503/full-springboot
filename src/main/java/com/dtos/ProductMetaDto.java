package com.dtos;

import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaDto {
    private Long id;
    private String metaKey;
    private String metaValue;

    public static ProductMetaDto toDto(ProductMetaEntity entity) {
        if(entity == null) new RuntimeException("ProductMeta is null");
        return ProductMetaDto.builder()
                .id(entity.getId())
                .metaKey(entity.getMetaKey())
                .metaValue(entity.getMetaValue())
                .build();
    }
}
