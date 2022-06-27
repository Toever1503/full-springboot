package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tbl_product_meta")
public class ProductMetaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_meta_id")
    private Long id;
    @Column(name = "meta_key")
    private String metaKey;
    @Column(name = "meta_value")
    private String metaValue;

    @Column(name = "product_id")
    private Long productId;
}
