package com.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionsModel {
    private Long id;
    private String optionName;
    private Integer quantity;
    private Double newPrice;
    private Double oldPrice;

    private Long productId;
}
