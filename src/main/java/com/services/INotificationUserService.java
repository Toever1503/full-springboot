package com.services;

import com.entities.NotificationUser;
import com.models.NotificationUserModel;

public interface INotificationUserService extends IBaseService<NotificationUser, NotificationUserModel, Long>{
    boolean setAllRead();
}
