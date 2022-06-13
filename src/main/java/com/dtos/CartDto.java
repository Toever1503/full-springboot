package com.dtos;

import com.entities.CartEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CartDto {
    private Long id;
    private OptionDto option;
    private Integer quantity;
    private ProductDto product;

    public static CartDto toDto(CartEntity cartEntity) {
        if(cartEntity == null) return null;
        return CartDto.builder()
                .id(cartEntity.getId())
                .quantity(cartEntity.getQuantity())
                .product(ProductDto.toDto(cartEntity.getProduct()))
                .build();
    }
}
