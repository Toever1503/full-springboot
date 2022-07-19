package com.dtos;

import com.entities.NotificationEntity;
import com.utils.SecurityUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Builder
@Data
public class NotificationDto {
    private Long id;
    private String image;
    private String title;
    private String category;
    private String status;
    private String contentExcerpt;
    private Date updatedDate;
    private Date createdDate;
    private Boolean isEdit;
    private String createdBy;
    private Integer viewed;
    private boolean isRead;

    private String url;

    private List<Object> attachFiles;


    public NotificationDto(Long id,
                           String image,
                           String title,
                           String status,
                           String contentExcerpt,
                           Date updatedDate,
                           Date createdDate,
                           Boolean isEdit,
                           String createdBy,
                           Integer viewed,
                           boolean isRead,
                           String attachFiles,
                           String category,
                           String url) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.status = status;
        this.contentExcerpt = contentExcerpt;
        this.updatedDate = updatedDate;
        this.createdDate = createdDate;
        this.isEdit = isEdit;
        this.createdBy = createdBy;
        this.viewed = viewed;
        this.isRead = isRead;
        this.attachFiles = attachFiles == null ? null : new JSONObject(attachFiles).getJSONArray("files").toList();
        this.category = category;
        this.url = url;
    }

    public NotificationDto(Long id, String image, String title, String category, String status, String contentExcerpt, Date updatedDate, Date createdDate, Boolean isEdit, String createdBy, Integer viewed, boolean isRead, String url, List<Object> attachFiles) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.category = category;
        this.status = status;
        this.contentExcerpt = contentExcerpt;
        this.updatedDate = updatedDate;
        this.createdDate = createdDate;
        this.isEdit = isEdit;
        this.createdBy = createdBy;
        this.viewed = viewed;
        this.isRead = isRead;
        this.url = url;
        this.attachFiles = attachFiles;
    }

    public NotificationDto isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public static NotificationDto toDto(NotificationEntity entity) {
        if (entity == null) return null;
        return NotificationDto.builder()
                .id(entity.getId())
                .image(entity.getImage())
                .status(entity.getStatus())
                .title(entity.getTitle())
                .contentExcerpt(entity.getContentExcerpt())
                .updatedDate(entity.getUpdatedDate())
                .createdDate(entity.getCreatedDate())
                .viewed(entity.getViewed())
                .createdBy(entity.getCreatedBy().getUserName())
                .isEdit(entity.getIsEdit())
                .attachFiles(entity.getAttachFiles() == null ? Collections.EMPTY_LIST : new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList())
                .category(entity.getCategory())
                .build();
    }


}
