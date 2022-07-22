package com.config;

import com.entities.OrderEntity;
import com.models.OrderModel;
import com.models.SocketNotificationModel;
import com.repositories.INotificationRepository;
import com.repositories.IOrderRepository;
import com.services.ISocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


public class ScheduledJobConfig {
    @Autowired
    INotificationRepository notificationRepository;
    @Autowired
    ISocketService socketService;
    @Autowired
    IOrderRepository orderRepository;
    //Cron for posting future notification
    // tham so trong cron la giay, phut, gio, ngay, thang, nam
    @Scheduled(cron = "0 0/5 * * * *")
    public void uploadNotification(){
        notificationRepository.postCronNotifications();
        List<Long> ids = this.notificationRepository.findAllFutureIds();
        for (Long id : ids) {
            socketService.sendNotificationForAllUser(SocketNotificationModel.toModel(this.notificationRepository.findById(id).get()));
        }
    }

    @Scheduled(cron = "0 0 0/12 * * *")
    public void updateOrderPayStatus(){
        orderRepository.changeOrderStatusByIDAndTime();
    }
}
