package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "tbl_notification_user", uniqueConstraints = { @UniqueConstraint(columnNames = { "notification_id", "user_id" }) })
public class NotificationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_user_id")
    private Long id;
    @Column(name = "is_read")
    private Boolean isRead;
    @Column(name = "notification_id")
    private Long notificationId;
    @Column(name = "user_id")
    private Long userId;
}
