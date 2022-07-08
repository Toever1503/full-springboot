package com.entities;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tbl_category")
public class CategoryEntity {
    public static final String FOLDER = "category/";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "slug", unique = true)
    private String slug;
    @Column(name = "description")
    private String description;
    @Column(name = "total_product")
    private Integer totalProduct;

    @Column(name = "deep_level")
    private Integer deepLevel;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private Boolean status;

    // image category
    @Column(name = "category_file")
    private String catFile;

    // image industry
    @Column(name = "category_file2")
    private String catFile2;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryEntity parentCategory;

    @ManyToOne
    @JoinColumn(name = "industry_id")
    private CategoryEntity industry;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<CategoryEntity> childCategories;

    @OneToMany(mappedBy = "industry", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<CategoryEntity> categories;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;

}
