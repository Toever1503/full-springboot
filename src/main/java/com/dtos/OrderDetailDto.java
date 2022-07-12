package com.dtos;

import com.entities.OrderDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetailDto {
    private Long id;
    private Double price;
    private Integer quantity;
    private String optionName;
    private ProductDto product;
    private Boolean isReview;
    private Long sku;

    public static OrderDetailDto toDto(OrderDetailEntity entity) {
        if(entity == null) return null;
        return OrderDetailDto.builder()
                .id(entity.getId())
                .sku(entity.getSku().getId())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .isReview(entity.getIsReview())
                .optionName(entity.getOption())
                .product(ProductDto.toDto(entity.getProductId()))
                .build();
    }
}
