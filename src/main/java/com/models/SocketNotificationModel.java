package com.models;

import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.entities.NotificationEntity;
import com.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SocketNotificationModel {
    private Long id;
    private String title;
    private String image;
    private String contentExcerpt;
    private ENotificationCategory category;
    private String url;
    private Date createdDate;
    private boolean read;
    public static final String DEFAULT_SYSTEM_LOGO = "https://team-2.s3.ap-northeast-2.amazonaws.com/static-images/LogoYDs.png";
    public SocketNotificationModel(Long id, String title, String contentExcerpt, ENotificationCategory category, String url) {
        this.id = id;
        this.title = title;
        this.contentExcerpt = contentExcerpt;
        this.category = category;
        this.url = url;
    }

    public static NotificationEntity toEntity(SocketNotificationModel model) {
        if (model == null) throw new RuntimeException("SocketNotificationModel is null");
        return NotificationEntity.builder()
                .title(model.getTitle())
                .image(model.getImage() == null ? DEFAULT_SYSTEM_LOGO : model.getImage())
                .contentExcerpt(model.getContentExcerpt())
                .category(model.getCategory().name())
                .url(model.getUrl())
                .isJustNotice(true)
                .build();
    }
    public static SocketNotificationModel toModel(NotificationEntity entity) {
        if (entity == null) throw new RuntimeException("NotificationEntity is null");
            return SocketNotificationModel.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .image(entity.getImage() == null ? DEFAULT_SYSTEM_LOGO : entity.getImage())
                    .contentExcerpt(entity.getContentExcerpt())
                    .url(entity.getUrl())
                    .createdDate(entity.getCreatedDate())
                    .read(false)
                    .build();
    }
}
