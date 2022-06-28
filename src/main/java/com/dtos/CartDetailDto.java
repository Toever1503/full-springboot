package com.dtos;

import com.entities.CartDetailEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CartDetailDto {
    private Long id;
    private ProductSkuDto productSkuDto;
    private Integer quantity;

    public static CartDetailDto toDto(CartDetailEntity cartDetail) {
        if(cartDetail == null) return null;
        return CartDetailDto.builder()
                .id(cartDetail.getId())
                .productSkuDto(ProductSkuDto.toDto(cartDetail.getSku()))
                .quantity(cartDetail.getQuantity())
                .build();
    }
}
