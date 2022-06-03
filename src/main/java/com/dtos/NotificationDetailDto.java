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
public class NotificationDetailDto {
    private Long id;
    private String title;
    private String content;
    private List<Object> attachFiles;
    private Date createdDate;
    private Date updatedDate;
    private Integer viewed;
    private boolean isEdit;
    private Integer limitEditCount;
    private String status;
    private Date futureDate;
    private String createdBy;

    public static NotificationDetailDto toDto(NotificationEntity entity){
        if(entity == null) throw new RuntimeException("NotificationEntity id " + entity.getId() + " is null");
        return NotificationDetailDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .attachFiles(entity.getAttachFiles()!=null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : null)
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .viewed(entity.getViewed())
                .isEdit(entity.getIsEdit())
                .limitEditCount(entity.getLimitEditCount())
                .status(entity.getStatus())
                .futureDate(entity.getFutureDate())
                .createdBy(entity.getCreatedBy().getUserName())
                .build();
    }

}
