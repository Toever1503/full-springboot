package com.config.socket;

import com.config.socket.exception.ChatRoomException;
import com.dtos.NotificationDto;
import com.dtos.SocketDtos.NotificationSocketDto;
import com.entities.NotificationEntity;
import com.google.gson.Gson;
import com.models.SocketNotificationModel;
import com.models.sockets.MessageData;
import com.models.sockets.SendTo;
import com.models.sockets.SocketMessage;
import com.services.CustomUserDetail;
import com.services.INotificationService;
import com.utils.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SocketHandler implements WebSocketHandler {
    public static ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.getAttributes().put("topics", new ArrayList<>());
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        session.getAttributes().put("username", customUserDetail.getUsername());
        userSessions.put(customUserDetail.getUser().getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Gson gson = new Gson();
        JSONObject receive = parseJson(String.valueOf(message.getPayload()));
        System.out.println("session = " + session + ", message = " + receive);
        SocketMessage socketMessage = new SocketMessage();
        if (receive.has("action")) {
            socketMessage.setAction(receive.getString("action"));
            if (receive.has("messageData"))
                socketMessage.setMessageData((MessageData) gson.fromJson(String.valueOf(receive.get("messageData")), MessageData.class));
        }

//        if (receive.has("sendTo")) {
//            String sendTo = receive.getString("sendTo");
//            if (sendTo.equals(SendTo.ALL.toString())) {
//                socketMessage.setSendTo(SendTo.ALL);
//                socketMessage.setUidSet(userSessions.keySet());
//            }
//            if (sendTo.equals(SendTo.USER.toString())) {
//                if (receive.has("idList")) {
//                    Set<Long> idSet = new HashSet<>();
//                    for (Object l : receive.getJSONArray("idList")
//                    ) {
//                        Long uid = Long.valueOf((Integer) l);
//                        idSet.add(uid);
//                    }
//                    socketMessage.setUidSet(idSet);
//                }
//            }
//        }
        socketMessage.setUidSet(userSessions.keySet());

        System.out.println(socketMessage.toString());
        if (socketMessage.getAction() != null && receive.has("topic")) {
            switch (socketMessage.getAction()) {
                case "publish":
                    try {
                        socketMessage.setTopic(receive.getString("topic"));
                        publishMessage(socketMessage, message);
                    } catch (RuntimeException e) {
                        throw new RuntimeException("Topic not found!");
                    }
                    break;
                case "subscribe":
                    try {
                        subscribeTopic(session, socketMessage, receive.getJSONArray("topics").toList(), true);
                    } catch (Exception e) {
                        throw new Exception(e.getMessage());
                    }
                    break;
                case "unsubscribe":
                    try {
                        subscribeTopic(session, socketMessage, receive.getJSONArray("topics").toList(), false);
                    } catch (Exception e) {
                        throw new Exception(e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (exception.getClass().getClass().equals(ChatRoomException.class)) {
            ChatRoomException chatRoomException = (ChatRoomException) exception;
            TextMessage message = new TextMessage(chatRoomException.toJson());
            session.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        userSessions.remove(userSessions.remove(customUserDetail.getUser().getId()));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public static JSONObject parseJson(String json) {
        System.out.println("json: " + json);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void subscribeTopic(WebSocketSession session, SocketMessage socketMessage, List<Object> topics, boolean subscribe) {
        if (topics == null)
            throw new RuntimeException("Topic is null");
        List userTopics = (List<String>) session.getAttributes().get("topics");
        if (subscribe) {
            for (Object topic : topics) {
                if (!userTopics.contains(topic)) {
                    userTopics.add(topic);
                }
            }
        } else {
            userTopics.removeAll(topics);
        }
    }

    private void publishMessage(SocketMessage socketMessage, WebSocketMessage<?> message) throws IOException {
        List<WebSocketSession> sendSessions = new ArrayList<>();
        for (Long uid : socketMessage.getUidSet()) {
            List<String> topics = (List<String>) userSessions.get(uid).getAttributes().get("topics");
            if (topics.contains(socketMessage.getTopic())) {
                sendSessions.add(userSessions.get(uid));
            }
        }
        System.out.println(sendSessions.toString());
        if (sendSessions.size() > 0) {
            for (WebSocketSession s : sendSessions
            ) {
                s.sendMessage(message);
            }
        }
    }

//    public void publishNotification(SocketMessage socketMessage, NotificationEntity notification, Long id) {
//        NotificationSocketMessage notificationSocketMessage = new NotificationSocketMessage(socketMessage.getTopic(), NotificationSocketDto.toNotificationSocketDto(notification));
//        String data = new JSONObject(notificationSocketMessage).toString();
//
//        WebSocketMessage mss = new TextMessage(data);
//        if (socketMessage.getSendTo().equals(SendTo.ALL))
//            userSessions.forEachValue(10l, s -> this.sendMessage(s, mss));
//        else {
//            userSessions.keySet().stream().filter(s -> s.longValue() == id).forEach(s -> this.sendMessage(userSessions.get(s.longValue()), mss));
//        }
//    }

    // use this
    public void publishNotification(SocketMessage socketMessage, NotificationEntity notification) {
        NotificationSocketMessage notificationSocketMessage = new NotificationSocketMessage(socketMessage.getTopic(), NotificationSocketDto.toNotificationSocketDto(notification));
        String data = new JSONObject(notificationSocketMessage).toString();
        WebSocketMessage mss = new TextMessage(data);
        if (socketMessage.getSendTo().equals(SendTo.ALL))
            userSessions.forEachValue(10l, s -> this.sendMessage(s, mss));
        else {
            socketMessage.getUidSet().forEach(uId -> this.sendMessage(userSessions.get(uId), mss));
        }
    }

    public void publishNotification(SocketNotificationModel notification, List<Long> uIds) {
        NotificationSocketMessage notificationSocketMessage = new NotificationSocketMessage("notification", notification);
        WebSocketMessage mss = new TextMessage(new JSONObject(notificationSocketMessage).toString());
        if (uIds == null)
            userSessions.forEachValue(10l, s -> this.sendMessage(s, mss));
        else uIds.forEach(uId -> this.sendMessage(userSessions.get(uId), mss));
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage<?> mss) {
        try {
            session.sendMessage(mss);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

