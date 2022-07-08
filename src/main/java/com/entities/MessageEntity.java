package com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbl_messages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "message")
    private String message;
    @Column(name = "attachment")
    private String attachment;
    @ManyToOne(targetEntity = UserEntity.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoomEntity chatRoomEntity;
    @CreationTimestamp
    @Column(name = "created_date")
    private Timestamp createdDate;
}
