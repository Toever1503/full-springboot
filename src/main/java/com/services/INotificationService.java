package com.services;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import com.models.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService extends IBaseService<NotificationEntity, NotificationModel, Long> {
     Page<NotificationDto> userGetAllNotifications(Pageable page);

}
