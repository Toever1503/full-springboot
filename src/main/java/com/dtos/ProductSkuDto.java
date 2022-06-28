package com.dtos;

import com.entities.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductSkuDto {
    private Long id;
    private Double price;
    private Integer discount; // similar with old price and new price. range from 1-100 percent.
    private String image;
    private String skuCode;
    private Integer inventoryQuantity;

    public static ProductSkuDto toDto(ProductSkuEntity entity) {
        if (entity == null) return null;
        return ProductSkuDto.builder()
                .id(entity.getId())
                .price(entity.getPrice())
                .skuCode(entity.getSkuCode())
                .discount(entity.getDiscount())
                .image(entity.getImage())
                .inventoryQuantity(entity.getInventoryQuantity())
                .build();
    }
}
