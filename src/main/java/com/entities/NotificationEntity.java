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
@Builder
@Data
@Entity
@Table(name = "tbl_notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @Column(name = "limit_edit_count")
    private Integer limitEditCount;
    @Column(name = "status")
    private String status;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "future_date")
    private Date futureDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;

    @OneToMany
    @JoinColumn(name = "notification_id")
    private List<NotificationUser> notificationUsers;

    public static final String FOLDER = "/notification/";
}
