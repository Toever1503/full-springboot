package com.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_chat_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatRoomEntity {
    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(
//            name = "uuid",
//            strategy = "uuid2"
//    )
    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;
    @OneToMany(targetEntity = MessageEntity.class,mappedBy = "chatRoomEntity", cascade = CascadeType.MERGE)
    private List<MessageEntity> messageEntities = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rooms_users",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> userEntities;
    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

}
