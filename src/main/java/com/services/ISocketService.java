package com.services;

import com.entities.NotificationEntity;
import com.entities.OrderEntity;
import com.entities.QuestionEntity;
import com.entities.ReviewEntity;
import com.models.NotificationModel;
import com.models.SocketNotificationModel;

import java.util.List;

public interface ISocketService {
    //    boolean sendOrderNotificationForSingleUser(OrderEntity order, Long userID, String url, String msg);
//    boolean sendQuestionNotificationForSingleUser(QuestionEntity question, Long userID, String url, String msg);
//    boolean sendReviewNotificationForSingleUser(ReviewEntity review, Long userID, String url, String msg);
    void sendNotificationForAllUser(SocketNotificationModel model);

    void sendNotificationForSpecificUser(SocketNotificationModel model, List<Long> userId);
}
