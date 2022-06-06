package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Data
@Entity
@Table(name = "tbl_notification")
public class NotificationEntity {
    public static final String FOLDER = "notification/";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image")
    private String image;
    @Column(name = "category")
    private String category;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "content_excerpt")
    private String contentExcerpt;
    @Column(name = "attach_files ")
    private String attachFiles;
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "viewed")
    private Integer viewed;
    @Column(name = "is_edit")
    private Boolean isEdit;

    public static Integer limitEditCount = 3;
    @Column(name = "count_edit")
    private Integer countEdit;
    @Column(name = "status")
    private String status;

    public static Integer limitEditMin = 5;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "future_date")
    private Date futureDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;
    public static final String FOLDER = "/notification/";
}
