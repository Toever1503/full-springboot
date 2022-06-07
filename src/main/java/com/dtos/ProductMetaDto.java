package com.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaDto {
    private Long id;
    private String metaKey;
    private String metaValue;
    private Long productId;
}
