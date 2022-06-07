package com.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaModel {
    private Long id;
    private String metaKey;
    private String metaValue;
    private Long productId;
}
