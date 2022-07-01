package com.entities;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tbl_product_skus", uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "sku_code"})})
public class ProductSkuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /*
     *  SKU code compile from variation values id, separated by dash (-). For examples: 1-2-4-5-6-7
     * each variation value id is separated by dash (-). For examples: 1-2-4-5-6-7
     * here is pattern for SKU code: ([1-9]+)|([1-9]+-([1-9]+-)+[1-9]+) , (\d+)|((\d+-)+\d+)
     */
    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "price")
    private Double price;

    @Column(name = "old_price")
    private Double oldPrice; // similar with old price and new price. range from 1-100 percent.

    @Column(name = "image")
    private String image;

    @Column(name = "inventoryQuantity")
    private Integer inventoryQuantity;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Column(name = "variation_size")
    private Integer variationSize;

    @Column(name = "option_name")
    private String optionName;

    public static final String SKU_CODE_PATTERN = "(\\d+)|((\\d+-)+\\d+)";

    /*
     * we don't join table because we don't know how many variation values will be there for a product sku. so we use skuCode instead
     */
//    private List<ProductVariationValueEntity> variationValues;
}
