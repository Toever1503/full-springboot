package com.dtos;

import com.entities.CartDetailEntity;
import com.entities.CartEntity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartDto {
    private Long id;
    private ProductDto productDto;
    private List<CartDetailDto> cartDetailDtos;
    private Integer totalQuantity;
    private Date updateDate;
    private Boolean isLiked;
    private List<ProductSkuDto> productSkuDtos;

    public static CartDto toDto(CartEntity cart) {
        if (cart == null) return null;
        return CartDto.builder()
                .id(cart.getId())
                .productDto(ProductDto.toDto(cart.getProduct()))
                .cartDetailDtos(cart.getCartDetails().stream().map(CartDetailDto::toDto).collect(Collectors.toList()))
                .totalQuantity(cart.getCartDetails().stream().mapToInt(CartDetailEntity::getQuantity).sum())
                .updateDate(cart.getUpdatedDate())
                .isLiked(cart.getIsLiked())
                .productSkuDtos(cart.getCartDetails().stream().map(CartDetailEntity::getSku).map(ProductSkuDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
