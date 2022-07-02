package com.dtos;

import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaDto {
    private Long id;
    @Field(type = FieldType.Keyword, storeNullValue = true)
    private String metaKey;
    @Field(type = FieldType.Keyword, storeNullValue = true)
    private String metaValue;

    public static ProductMetaDto toDto(ProductMetaEntity entity) {
        if(entity == null) return null;
        return ProductMetaDto.builder()
                .id(entity.getId())
                .metaKey(entity.getMetaKey())
                .metaValue(entity.getMetaValue())
                .build();
    }
}
