package com.models;

import com.entities.CartEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartModel {
    private Long id;
    private Long productId;
    private Long skuId;
    private Integer quantity;
}
