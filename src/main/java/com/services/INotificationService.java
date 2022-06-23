package com.services;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import com.models.NotificationModel;
import com.models.SocketNotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface INotificationService extends IBaseService<NotificationEntity, NotificationModel, Long> {
    Page<NotificationDto> userGetAllNotifications(Pageable page);

    void addForSpecificUser(SocketNotificationModel model,  List<Long> userId);

    void addSocketNotificationForAll(SocketNotificationModel model);

    boolean increaseView(long id);

    boolean setAllRead();

}
