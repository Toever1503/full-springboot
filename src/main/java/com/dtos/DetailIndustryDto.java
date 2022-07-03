package com.dtos;

import com.config.elasticsearch.ElasticsearchIndices;
import com.entities.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(indexName = ElasticsearchIndices.INDUSTRY_INDEX)
public class DetailIndustryDto {
    @Id
    private Long id;

    private String industryName;

    @Field(type = FieldType.Keyword)
    private String slug;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Nested, storeNullValue = true)
    private List<CategoryDto> categories;

    @Field(type = FieldType.Nested, storeNullValue = true)
    private List<ProductMetaDto2> productMetas;

    @Field(type = FieldType.Nested, storeNullValue = true)
    private List<ProductVariationDto2> productVariations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductMetaDto2 {
        private String metaKey;
        private List<String> metaValues;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductVariationDto2 {
        private String variationName;
        private List<String> variationValues;
    }

    public static DetailIndustryDto toDto(CategoryEntity industry) {
        if (industry == null) return null;
        return DetailIndustryDto.builder()
                .id(industry.getId())
                .industryName(industry.getCategoryName())
                .slug(industry.getSlug())
                .description(industry.getDescription())
                .categories(industry.getCategories() == null ? null : industry.getCategories().stream().map(child -> CategoryDto.toDto(child, true)).collect(Collectors.toList()))
                .build();
    }
}
