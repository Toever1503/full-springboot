package com.models;

import com.entities.OptionEntity;
import com.entities.ProductEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionModel {
    private Long id;
    private String optionName;
    private Integer quantity;
    private Double newPrice;
    private Double oldPrice;

    public static OptionEntity toEntity(OptionModel model, Long p) {
        if(model == null) new RuntimeException("ProductModel is null");
        return OptionEntity.builder()
                .id(model.getId())
                .optionName(model.getOptionName())
                .quantity(model.getQuantity())
                .newPrice(model.getNewPrice())
                .oldPrice(model.getOldPrice())
                .productId(p)
                .build();
    }
}
