package com.config;

import com.entities.NotificationEntity;
import com.repositories.NotificationRepository;
import com.services.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class ScheduledJobConfig {
    @Autowired
    NotificationRepository notificationRepository;

    @Scheduled(cron = "0 30 * * * *")
    public void uploadNotification(){
        notificationRepository.postCronNotifications();
    }
}
