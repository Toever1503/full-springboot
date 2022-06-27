package com.config;

import com.repositories.INotificationRepository;
import com.repositories.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;


public class ScheduledJobConfig {
    @Autowired
    INotificationRepository notificationRepository;
    @Autowired
    IOrderRepository orderRepository;
    //Cron for posting future notification
    // tham so trong cron la giay, phut, gio, ngay, thang, nam
    @Scheduled(cron = "0 30 * * * *")
    public void uploadNotification(){
        notificationRepository.postCronNotifications();
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void updateOrderPayStatus(){
        // later
//        orderRepository.changeOrderStatusByIDAndTime();
    }
}
