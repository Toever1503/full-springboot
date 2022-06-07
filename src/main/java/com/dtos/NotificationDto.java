package com.dtos;

import com.entities.NotificationEntity;
import com.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Builder
@Data
public class NotificationDto {
    private Long id;
    private String image;
    private String title;
    private String contentExcerpt;
    private Date updatedDate;
    private Date createdDate;
    private Boolean isEdit;
    private String createdBy;
    private Integer viewed;
    private boolean isRead;

    public NotificationDto(Long id, String image, String title, String contentExcerpt, Date updatedDate, Date createdDate, Boolean isEdit, String createdBy, Integer viewed, boolean isRead) {
        this.id = id;
        this.image= image;
        this.title = title;
        this.contentExcerpt = contentExcerpt;
        this.updatedDate = updatedDate;
        this.createdDate = createdDate;
        this.isEdit = isEdit;
        this.createdBy = createdBy;
        this.viewed = viewed;
        this.isRead = isRead;
    }

    public NotificationDto isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }
    public static NotificationDto toDto(NotificationEntity entity){
        if(entity == null) throw new RuntimeException("NotificationEntity id " + entity.getId() + " is null");
        return NotificationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .contentExcerpt(entity.getContentExcerpt())
                .updatedDate(entity.getUpdatedDate())
                .createdDate(entity.getCreatedDate())
                .viewed(SecurityUtils.hasRole("ADMIN") ? entity.getViewed() : null)
                .createdBy(entity.getCreatedBy().getUserName())
                .isEdit(entity.getIsEdit())
                .build();
    }
}
