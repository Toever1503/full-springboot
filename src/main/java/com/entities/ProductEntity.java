package com.entities;

import com.utils.SecurityUtils;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "total_like")
    private Integer totalLike;
    @Column(name = "total_review")
    private Integer totalReview;
    @Column(name = "rating")
    private Float rating;
    @Column(name = "image")
    private String image;
    @Column(name = "attach_files")
    private String attachFiles;

    /*
     * soft delete
     * range: draft, published, deleted
     */
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private java.util.Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_Date")
    private java.util.Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private CategoryEntity industry;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private List<ProductMetaEntity> productMetas;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("price ASC")
    private List<ProductSkuEntity> skus;

    // use to check whether product is use variation or not
    @Column(name = "is_use_variation")
    private Boolean isUseVariation;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariationEntity> variations;

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "tbl_tag_products",
            joinColumns = @JoinColumn(name = "products_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    private Set<TagEntity> tags;

    public static String FOLDER = "/product/";
}
