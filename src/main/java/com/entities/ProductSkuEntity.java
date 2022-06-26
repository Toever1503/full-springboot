package com.entities;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tbl_product_skus")
public class ProductSkuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    /*
     *  SKU code compile from variation values id, separated by dash (-). For examples: 1-2-4-5-6-7
     * each variation value id is separated by dash (-). For examples: 1-2-4-5-6-7
     * here is pattern for SKU code: [1-9]+-([1-9]+-)+[1-9]+
     */
    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Integer discount; // similar with old price and new price. range from 1-100 percent.

    @Column(name = "image")
    private String image;

    /*
     * we don't join table because we don't know how many variation values will be there for a product sku. so we use skuCode instead
     */
//    private List<ProductVariationValueEntity> variationValues;
}
