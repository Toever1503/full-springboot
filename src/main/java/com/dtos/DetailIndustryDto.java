package com.dtos;

import com.entities.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DetailIndustryDto {
    private Long id;

    private String industryName;

    private String slug;

    private String description;

    private List<CategoryDto> categories;

    private List<ProductMetaDto> productMetas;

    private List<ProductVariationDto> productVariations;

    public static DetailIndustryDto toDto(CategoryEntity industry){
        if(industry == null) return null;
        return DetailIndustryDto.builder()
                .id(industry.getId())
                .industryName(industry.getCategoryName())
                .slug(industry.getSlug())
                .description(industry.getDescription())
                .categories(industry.getChildCategories() == null ? null : industry.getChildCategories().stream().map(child -> CategoryDto.toDto(child, true)).collect(Collectors.toList()))
                .build();
    }
}
