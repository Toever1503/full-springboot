package com.models;

import com.entities.ProductEntity;
import com.entities.ProductSkuEntity;
import com.utils.FileUploadProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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
    private Integer discount; // similar with old price and new price. range from 1-100 percent.

    private MultipartFile image;

    private String originImage;

    @NotNull
    private Integer inventoryQuantity;

    private List<Long> variationValues = new ArrayList<>();

    public static ProductSkuEntity toEntity(ProductSkuModel model, ProductEntity product, boolean isVariation) {
        if (model == null) throw new RuntimeException("Sku model is null");
        String skuCode = null;

        // check and handle sku code
        if (isVariation && model.variationValues.isEmpty())
            throw new RuntimeException("Product is using variation and sku model not have any variation value. Please check again!");
        else {
            skuCode = model.variationValues.stream().map(n -> n.toString()).reduce((str1, str2) -> str1.concat("-".concat(str2))).orElse(null);
            if (!skuCode.matches(ProductSkuEntity.SKU_CODE_PATTERN))
                throw new RuntimeException("Generated skuCode is invalid: ".concat(skuCode).concat(". Please check again!"));
        }

        ProductSkuEntity sku = ProductSkuEntity.builder()
                .product(product)
                .id(model.id)
                .price(model.price)
                .discount(model.discount == null ? 0 : model.discount)
                .inventoryQuantity(model.inventoryQuantity)
                .skuCode(skuCode)
                .build();

        return sku;
    }

    public static void main(String[] args) {
        boolean is = false;
        List<Long> ls = new ArrayList<>();

        System.out.println(is && ls.isEmpty());
//        List<Long> ids = new ArrayList<>();
//        ids.add(91l);
////        ids.add(22l);
////        ids.add(3l);
////        ids.add(35l);
//        String code = ids.stream().map(n -> n.toString()).reduce((str1, str2) -> str1.concat("-".concat(str2))).orElse(null);
//
//        System.out.println("code: " + code);
//        System.out.println("is match: " + code.matches("([1-9]+)|([1-9]+-([1-9]+-)+[1-9]+)"));
    }
}
