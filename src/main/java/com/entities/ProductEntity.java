package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tbl_product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "total_quantity")
    private Long totalQuantity;
    @Column(name = "total_like")
    private Long totalLike;
    @Column(name = "total_review")
    private Long totalReview;
    @Column(name = "rating")
    private Long rating;
    @Column(name = "avater")
    private String avater;
    @Column(name = "attach_files")
    private String attachFiles;
    @Column(name = "slugs")
    private String slugs;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductMetaEntity> productMeta;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OptionsEntity> options;
}
