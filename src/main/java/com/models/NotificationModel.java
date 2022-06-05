package com.models;

import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.entities.NotificationEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationModel {
    private Long id;
    private MultipartFile image;
    private ENotificationCategory category;
    private String title;
    private String content;
    private String contentExcerpt;
    private List<MultipartFile> attachFiles;
    private List<String> attachFilesOrigin = new ArrayList<>();
    private Integer viewed;
    private Boolean isEdit;
    private Integer limitEditCount;
    private Integer limitEditMin;
    private ENotificationStatus status;
    private Date futureDate;

    public static NotificationEntity toEntity(NotificationModel model) {
        if(model == null) return null;
        return NotificationEntity.builder()
                .id(model.getId())
                .category(model.getCategory().name())
                .title(model.getTitle())
                .content(model.getContent())
                .contentExcerpt(model.getContentExcerpt())
                .viewed(0)
                .isEdit(false)
                .limitEditCount(model.getLimitEditCount() == null ? 3 : model.getLimitEditCount())
                .countEdit(0)
                .limitEditMin(model.getLimitEditMin() == null ? 15 : model.getLimitEditMin())
                .status(model.getStatus().name())
                .futureDate(model.getFutureDate() == null ? null : new Date(model.getFutureDate().getTime()))
                .build();
    }
}
