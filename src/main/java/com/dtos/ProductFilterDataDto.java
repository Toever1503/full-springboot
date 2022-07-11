package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductFilterDataDto {
    private List<CategoryDto> categoryFilter;
    private List<ProductVariationDto2> variationFilter;
    private List<ProductMetaDto2> metaFilter;
    private List<String> tags;

}
