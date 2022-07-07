package com.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_banner")
public class BannerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_banner")
    private String name;
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
    @Column(name = "status")
    private String status;
    @Column(name = "is_edit")
    private Boolean isEdit;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;
}
