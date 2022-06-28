package com.dtos;

import com.entities.ProductVariationValueEntity;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductVariationValueDto {
    private Long variationValueId;
    private String value;
    private Boolean isChecked;

    public static ProductVariationValueDto toDto(ProductVariationValueEntity entity) {
        if (entity == null) return null;
        return ProductVariationValueDto.builder()
                .variationValueId(entity.getId())
                .value(entity.getValue())
                .isChecked(false)
                .build();
    }

    public static void main(String[] args) {
        Gson s = new Gson();
    }
}
