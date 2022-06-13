package com.models;

import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductMetaModel {
    @ApiModelProperty(notes = "Product ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Product meta key", dataType = "String", example = "size")
    @NotNull
    @NotBlank
    private String metaKey;
    @ApiModelProperty(notes = "Product meta value", dataType = "String", example = "L")
    @NotNull
    @NotBlank
    private String metaValue;

    public static ProductMetaEntity toEntity(ProductMetaModel model, Long product) {
        if(model == null) new RuntimeException("ProductModel is null");
        return ProductMetaEntity.builder()
                .id(model.getId())
                .metaKey(model.getMetaKey())
                .metaValue(model.getMetaValue())
                .productId(product)
                .build();
    }
}
