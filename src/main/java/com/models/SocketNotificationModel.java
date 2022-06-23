package com.models;

import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.entities.NotificationEntity;
import com.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SocketNotificationModel {
    private Long id;
    private String title;
    private String contentExcerpt;
    private String url;

    public static NotificationEntity toEntity(SocketNotificationModel model) {
        if (model == null) throw new RuntimeException("SocketNotificationModel is null");
        return NotificationEntity.builder()
                .title(model.getTitle())
                .contentExcerpt(model.getContentExcerpt())
                .url(model.getUrl())
                .build();
    }
    public static SocketNotificationModel toModel(NotificationEntity entity) {
        if (entity == null) throw new RuntimeException("NotificationEntity is null");
        return SocketNotificationModel.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .contentExcerpt(entity.getContentExcerpt())
                .url(entity.getUrl())
                .build();
    }
}
