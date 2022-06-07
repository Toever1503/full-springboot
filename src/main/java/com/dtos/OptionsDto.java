package com.dtos;

import com.entities.OptionEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionsDto {
    private Long id;
    private String optionName;
    private Integer quantity;
    private Double newPrice;
    private Double oldPrice;

    private Long productId;

    public static OptionsDto toDto(OptionEntity entity){
        OptionsDto dto = new OptionsDto();
        dto.setId(entity.getId());
        dto.setOptionName(entity.getOptionName());
        dto.setQuantity(entity.getQuantity());
        dto.setNewPrice(entity.getNewPrice());
        dto.setOldPrice(entity.getOldPrice());
        dto.setProductId(entity.getProduct().getId());
        return dto;
    }
}
