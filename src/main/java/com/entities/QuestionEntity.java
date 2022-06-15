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
@Table(name = "tbl_question")
@Entity
@Builder
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category")
    private String category;
    @Column(name = "title")
    private String title;
    @Column(name = "quest_content")
    private String questContent;
    @Column(name = "quest_file")
    private String questFile;
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "reply_content")
    private String replyContent;
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "isCompatible")
    private boolean isCompatible;
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
    @ManyToOne
    @JoinColumn(name = "answered_by")
    private UserEntity answeredBy;

    @Column(name="limit_edit_min")
    private Integer limitEditMin;

    public static final String FOLDER = "/question/";
}
