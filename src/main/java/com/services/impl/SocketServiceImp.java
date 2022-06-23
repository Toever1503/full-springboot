package com.services.impl;

import com.config.socket.SocketHandler;
import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.entities.NotificationEntity;
import com.entities.OrderEntity;
import com.entities.QuestionEntity;
import com.entities.ReviewEntity;
import com.models.NotificationModel;
import com.models.OrderModel;
import com.models.SocketNotificationModel;
import com.models.sockets.SendTo;
import com.models.sockets.SocketMessage;
import com.services.INotificationService;
import com.services.ISocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SocketServiceImp implements ISocketService {
    @Autowired
    SocketHandler socketHandler;

//    @Override
//    public boolean sendOrderNotificationForSingleUser(OrderEntity order, Long userID, String url, String msg) {
//        SocketMessage socketMessage = new SocketMessage("publish",null,"notification", SendTo.USER, Set.of(userID));
//        NotificationModel notification = NotificationModel.builder().category(ENotificationCategory.ORDER).title(msg+ order.getId()).content(order.getStatus()).status(ENotificationStatus.POSTED).build();
//        try{
//            socketHandler.publishNotification(socketMessage,notificationService.addForSpecificUser(notification,userID,url));
//            return true;
//        }catch (Exception e){
//            return false;
//        }
//
//
//    }
//
//    @Override
//    public boolean sendQuestionNotificationForSingleUser(QuestionEntity question, Long userID, String url, String msg) {
//        SocketMessage socketMessage = new SocketMessage("publish",null,"notification", SendTo.USER,Set.of(userID));
//        NotificationModel notification = NotificationModel.builder().category(ENotificationCategory.QUESTION).title(msg + question.getTitle()).content(question.getStatus()).status(ENotificationStatus.POSTED).build();
//        try{
//            socketHandler.publishNotification(socketMessage,notificationService.addForSpecificUser(notification,userID,url));
//            return true;
//        }catch (Exception e){
//            return false;
//        }
//    }
//
//    @Override
//    public boolean sendReviewNotificationForSingleUser(ReviewEntity review, Long userID, String url, String msg) {
//        SocketMessage socketMessage = new SocketMessage("publish",null,"notification", SendTo.USER,Set.of(userID));
//        NotificationModel notification = NotificationModel.builder().category(ENotificationCategory.REVIEW).title(msg + review.getStatus()).content(review.getStatus()).status(ENotificationStatus.POSTED).build();
//        try{
//            socketHandler.publishNotification(socketMessage,notificationService.addForSpecificUser(notification,userID,url));
//            return true;
//        }catch (Exception e){
//            return false;
//        }
//    }


    @Override
    public void sendNotificationForAllUser(SocketNotificationModel model) {
        this.socketHandler.publishNotification(model, null);
    }

    @Override
    public void sendNotificationForSpecificUser(SocketNotificationModel model, List<Long> userId) {
        this.socketHandler.publishNotification(model, userId);
    }
}
