package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
    private Integer totalQuantity;
    @Column(name = "total_like")
    private Integer totalLike;
    @Column(name = "total_review")
    private Integer totalReview;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "image")
    private String image;
    @Column(name = "attach_files")
    private String attachFiles;
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "active")
    private Boolean active;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private java.util.Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_Date")
    private java.util.Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductMetaEntity> productMetas;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OptionEntity> options;

    @ManyToMany(mappedBy = "products",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<TagEntity> tags;

    public static String FOLDER = "product/";
}
