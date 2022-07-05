package com.dtos;

import com.entities.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductSkuDto {
    private Long id;
    private Double price;
    private Double oldPrice; // similar with old price and new price. range from 1-100 percent.
    private String imageUrl;
    private String skuCode;
    private Integer inventoryQuantity;
    private String productName;
    private Boolean isValid;
    private List<Long> variationValues;
    private String optionName;

    public static ProductSkuDto toDto(ProductSkuEntity entity) {
        if (entity == null) return null;
        return ProductSkuDto.builder()
                .id(entity.getId())
                .price(entity.getPrice())
                .skuCode(entity.getSkuCode())
                .oldPrice(entity.getOldPrice())
                .imageUrl(entity.getImage())
                .inventoryQuantity(entity.getInventoryQuantity())
                .isValid(entity.getIsValid())
                .optionName(entity.getOptionName())
                .productName(entity.getProduct().getName())
                .variationValues(entity.getSkuCode() != null ? Arrays.stream(entity.getSkuCode().split("-")).map(Long::parseLong).collect(Collectors.toList()): null)
                .build();
    }

    public static void main(String[] args) {
        String  s = "245";
        System.out.println(Arrays.stream(s.split("-")).map(Long::parseLong).collect(Collectors.toList()));
    }
}
