package com.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OptionsDto {
    private Long id;
    private String optionName;
    private Long quantity;
    private Long newPrice;
    private Long oldPrice;

    private Long productId;
}
