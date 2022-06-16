package com.dtos;

import com.entities.OptionEntity;
import com.entities.ProductEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionDto {
    private Long id;
    private String optionName;
    private Integer quantity;
    private Double newPrice;
    private Double oldPrice;

    public static OptionDto toDto(OptionEntity entity) {
        if(entity == null) return null;
        return OptionDto.builder()
                .id(entity.getId())
                .optionName(entity.getOptionName())
                .quantity(entity.getQuantity())
                .newPrice(entity.getNewPrice())
                .oldPrice(entity.getOldPrice())
                .build();
    }
}
