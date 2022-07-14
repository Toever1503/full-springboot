package com.entities.chat;

import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_chat_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @OneToMany(targetEntity = ChatMessageEntity.class, mappedBy = "chatRoom")
    private List<ChatMessageEntity> messages;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createdBy;

    @CreationTimestamp
    @Column(name = "created_date")
    private Date createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private Date updatedDate;

    public static final String FOLDER = "/chat/";
}
