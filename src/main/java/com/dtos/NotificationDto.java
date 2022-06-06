package com.dtos;

import com.entities.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationDto {
    private Long id;
    private String image;
    private String title;
    private String contentExcerpt;
    private Date updatedDate;
    private Boolean isEdit;
    private String createdBy;
    private boolean isRead;
    public NotificationDto isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public static NotificationDto toDto(NotificationEntity entity){
        if(entity == null) throw new RuntimeException("NotificationEntity id " + entity.getId() + " is null");
        return NotificationDto.builder()
                .id(entity.getId())
                .image(entity.getImage())
                .title(entity.getTitle())
                .contentExcerpt(entity.getContentExcerpt())
                .updatedDate(entity.getUpdatedDate())
                .createdBy(entity.getCreatedBy().getUserName())
                .isEdit(entity.getIsEdit())
                .build();
    }
}
