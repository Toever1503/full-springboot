package com.models;

import com.entities.ProductEntity;
import com.entities.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductSkuModel {

    private Long id;
    @NotNull
    private Double price;
    private Double oldPrice; // similar with old price and new price. range from 1-100 percent.

    private String imageParameter;

    private String originImage;

    @NotNull
    private Integer inventoryQuantity;

    private List<String> variationValueNames;

    private List<Long> variationValues;

    private Boolean isValid = false;
    private Integer variationSize = 0;

    public static ProductSkuEntity toEntity(ProductSkuModel model, ProductEntity product) {
        if (model == null) throw new RuntimeException("Sku model is null");
        String skuCode = null;

        if(model.getVariationValues() != null){
            // check and handle sku code
            if (product.getIsUseVariation() && model.variationValues.isEmpty())
                throw new RuntimeException("Product is using variation and sku model not have any variation value. Please check again!");
            else if (product.getIsUseVariation() && !model.variationValues.isEmpty()) {
                skuCode = model.variationValues.stream().map(n -> n.toString()).reduce((str1, str2) -> str1.concat("-".concat(str2))).orElse(null);
                if (!skuCode.matches(ProductSkuEntity.SKU_CODE_PATTERN))
                    throw new RuntimeException("Generated skuCode is invalid: ".concat(skuCode).concat(". Please check again!"));
            }
        }

        ProductSkuEntity sku = ProductSkuEntity.builder()
                .product(product)
                .id(model.id)
                .price(model.price)
                .oldPrice(model.oldPrice)
                .inventoryQuantity(model.inventoryQuantity)
                .skuCode(skuCode)
                .image(model.originImage)
                .isValid(model.isValid)
                .variationSize(model.variationSize)
                .build();
        return sku;
    }

    public static void main(String[] args) {
        List<Long> ids = new ArrayList<>();
        ids.add(91l);
        ids.add(22l);
        ids.add(3l);
//        ids.add(35l);
        String code = ids.stream().map(n -> n.toString()).reduce((str1, str2) -> str1.concat("-".concat(str2))).orElse(null);

        System.out.println("code: " + code);
        System.out.println("is match: " + code.matches("(\\d+)|((\\d+-)+\\d+)"));
    }
}
