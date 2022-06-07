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
@Table(name = "tbl_product_option")
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "new_price")
    private Double newPrice;

    @Column(name = "old_price")
    private Double oldPrice;

    @ManyToOne(fetch =
            FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
