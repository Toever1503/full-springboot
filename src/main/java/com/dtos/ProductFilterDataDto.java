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
    private List<IndustryDto> industryFilter;
    private List<CategoryDto> categoryFilter;
    private List<DetailIndustryDto.ProductVariationDto2> variationFilter;
    private List<DetailIndustryDto.ProductMetaDto2> metaFilter;
    private List<String> tags;

}
