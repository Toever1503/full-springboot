package com.dtos.SocketDtos;

import com.entities.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSocketDto {
    private String title;
    private Date createdDate;
    private String url;

    public static NotificationSocketDto toNotificationSocketDto(NotificationEntity entity){
        return NotificationSocketDto.builder().title(entity.getTitle()).url(entity.getUrl()).createdDate(entity.getCreatedDate()).build();
    }
}
