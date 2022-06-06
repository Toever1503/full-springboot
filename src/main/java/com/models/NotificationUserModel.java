package com.models;

import com.entities.NotificationEntity;
import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationUserModel {
    private Long id;
    private boolean isRead;
    private NotificationEntity notificationId;
    private Long userId;
}
