package com.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangeOptionProductModel {
    private Long productId;
    private Long optionId;
}
