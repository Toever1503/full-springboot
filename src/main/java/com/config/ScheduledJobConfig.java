package com.config;

import com.repositories.INotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;


public class ScheduledJobConfig {
    @Autowired
    INotificationRepository notificationRepository;
    //Cron for posting future notification
    @Scheduled(cron = "0 30 * * * *")
    public void uploadNotification(){
        notificationRepository.postCronNotifications();
    }
}
