package com.dtos;

import com.entities.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationDetailDto {
    private Long id;
    private String title;
    private String content;
    private String contentExcerpt;
    private String category;
    private String image;
    private List<Object> attachFiles;
    private Date createdDate;
    private Date updatedDate;
    private Integer viewed;
    private boolean isEdit;
    private String status;
    private Date futureDate;
    private String createdBy;

    public static NotificationDetailDto toDto(NotificationEntity entity){
        if(entity == null) return null;
        return NotificationDetailDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .image(entity.getImage())
                .contentExcerpt(entity.getContentExcerpt())
                .category(entity.getCategory())
                .attachFiles(entity.getAttachFiles()!=null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : Collections.EMPTY_LIST)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .viewed(entity.getViewed())
                .isEdit(entity.getIsEdit())
                .status(entity.getStatus())
                .futureDate(entity.getFutureDate())
                .createdBy(entity.getCreatedBy().getUserName())
                .build();
    }

}
