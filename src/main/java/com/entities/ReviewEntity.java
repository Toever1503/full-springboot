package com.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_review")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    @Column(name = "option_name")
    private String optionName;
    @Column(name = "content")
    private String content;
    @Column(name = "rating")
    private String rating;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private java.util.Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_Date")
    private java.util.Date updatedDate;

    @Column(name = "attach_files")
    private String attachFiles;

    @Column(name = "is_edit")
    private Boolean isEdit;
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ReviewEntity parentReview;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    private OrderDetailEntity orderDetail;

    public static final String FOLDER = "/review/";
    public static final String REVIEW_URL = "/review/";
    public static final String ADMIN_REVIEW_URL = "/review/";

}
