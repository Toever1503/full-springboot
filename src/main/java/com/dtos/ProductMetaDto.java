package com.dtos;

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

    public static ProductMetaDto toDto(ProductMetaEntity entity){
        ProductMetaDto productMetaDto = new ProductMetaDto();
        productMetaDto.setMetaKey(entity.getMetaKey());
        productMetaDto.setMetaValue(entity.getMetaValue());
        productMetaDto.setId(entity.getId());
        return productMetaDto;
    }
}
