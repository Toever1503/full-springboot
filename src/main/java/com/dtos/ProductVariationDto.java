package com.dtos;

import com.entities.ProductVariationEntity;
import com.models.ProductVariationValueModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductVariationDto {
    private Long variationId;
    @Field(type = FieldType.Keyword, storeNullValue = true)
    private String variationName;

    @Field(type = FieldType.Nested, name = "values", storeNullValue = true)
    private List<ProductVariationValueDto> values;

    public static ProductVariationDto toDto(ProductVariationEntity entity) {
        if (entity == null) return null;
        return ProductVariationDto.builder()
                .variationId(entity.getId())
                .variationName(entity.getVariationName())
                .values(entity.getVariationValues() == null ? null : entity.getVariationValues().stream().map(ProductVariationValueDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
