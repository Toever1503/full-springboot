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
    private String optionId;
    private Double price;
    private Integer quantity;
    private ProductDto product;

    public static OrderDetailDto toDto(OrderDetailEntity entity) {
        if(entity == null) return null;
        return OrderDetailDto.builder()
                .id(entity.getId())
                .optionId(entity.getOptionId())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .product(ProductDto.toDto(entity.getProductId()))
                .build();
    }
}
