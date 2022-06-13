package com.models;

import com.entities.NotificationEntity;
import com.entities.UserEntity;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Notification ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "user readed ?", dataType = "Boolean", example = "true")
    private boolean isRead;
    @ApiModelProperty(notes = "Notification", dataType = "NotificationEntity", example = " Object　of type NotificationEntity")
    private NotificationEntity notificationId;
    @ApiModelProperty(notes = "user id", dataType = "Long", example = "1")
    private Long userId;
}
