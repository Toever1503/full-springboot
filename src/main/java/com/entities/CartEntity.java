package com.entities;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "tbl_cart", uniqueConstraints = {@UniqueConstraint(columnNames = {"sku_id", "user_id", "cart_id"})})
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    @OrderBy("product.id ASC") // sort by product id ASC
    private ProductSkuEntity sku;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
