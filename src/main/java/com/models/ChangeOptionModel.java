package com.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeOptionModel {
    private Long id;
    private Long productId;
    private Long skuIdOld;
    private Long skuIdNew;
    private Integer quantity;
}
