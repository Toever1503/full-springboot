package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DetailProductDto {
    private ProductDto data;
    private Page<ProductDto> similarProducts;
}
