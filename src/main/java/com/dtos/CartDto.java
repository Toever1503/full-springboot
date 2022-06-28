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
    private Long productId;
    private List<Long> cartDetailId;
    private Date updateDate;

    public static CartDto toDto(CartEntity cart) {
        if(cart == null) return null;
        return CartDto.builder()
                .id(cart.getId())
                .productId(cart.getProduct().getId())
                .cartDetailId(cart.getCartDetails().stream().map(CartDetailEntity::getId).collect(Collectors.toList()))
                .updateDate(cart.getUpdatedDate())
                .build();
    }
}
