package com.config.socket;

import com.config.socket.exception.ChatRoomException;
import com.entities.UserEntity;
import com.google.gson.Gson;
import com.models.SocketNotificationModel;
import com.models.sockets.*;
import com.services.CustomUserDetail;
import com.utils.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class SocketHandler implements WebSocketHandler {
    public static ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    public static Map<String, ChatModel> chatRooms = new ConcurrentHashMap<>();
//    public static List<String> availableRooms = new ArrayList<>();
//    public static List<String> fullRooms = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.getAttributes().put("topics", new ArrayList<>());
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        session.getAttributes().put("username", customUserDetail.getUsername());
        session.getAttributes().put("id", customUserDetail.getUser().getId());
        userSessions.put(customUserDetail.getUser().getId(), session);
    }

    public static UserEntity getUserFromSession(WebSocketSession session) {
        return ((CustomUserDetail)((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal()).getUser();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Gson gson = new Gson();
        JSONObject receive = parseJson(String.valueOf(message.getPayload()));
        System.out.println("session = " + session + ", message = " + receive);
        SocketMessage socketMessage = new SocketMessage();
        if (receive.has("action")) {
            socketMessage.setAction(receive.getString("action"));
            if (receive.has("messageData")) {
                socketMessage.setMessageData((MessageData) gson.fromJson(String.valueOf(receive.get("messageData")), MessageData.class));
            }
            if (receive.getString("action").equals("joinChatRoom") || receive.getString("action").equals("sendChatMessage")) {
                if (!receive.has("roomId")) {
                    throw new RuntimeException("No chat room found!!!");
                }
            }
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
        Long curUid = Long.valueOf(session.getAttributes().get("id").toString());
        System.out.println(socketMessage.toString());
        if (socketMessage.getAction() != null && receive.has("topic")) {
            switch (socketMessage.getAction()) {
//                case "createChatRoom":
//                    try {
//                        if(receive.getString("topic").equals("chat")){
//                            ChatModel chatRoom = new ChatModel(session, UUID.randomUUID().toString(), false,false);
//                            chatRooms.add(chatRoom);
//                            availableRooms.add(chatRoom.getRoomId());
//                            SocketChatMessage socketChatMessage = new SocketChatMessage(null,new MessageData(),"chat");
//                            socketChatMessage.getMessageData().setMessageContent(session.getAttributes().get("username").toString()+" da tao phong chat: " + chatRoom.getRoomId());
//                            socketChatMessage.getMessageData().setCreatedDate(Calendar.getInstance().getTime());
//                            socketChatMessage.setRoomId(chatRoom.getRoomId());
//                            WebSocketMessage mss = new TextMessage(new JSONObject(socketChatMessage).toString());
//                            chatRoom.sendMessage(session,mss);
//                            session.sendMessage(mss);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case "joinChatRoom":
//                    try{
//                        if(receive.getString("topic").equals("chat")) {
//                            String roomId = receive.getString("roomId");
//                            ChatModel chatRoom = chatRooms.stream().filter(s -> s.getRoomId().equals(roomId)).findFirst().get();
//                            chatRoom.setFull(true);
//                            availableRooms.remove(chatRoom.getRoomId());
//                            fullRooms.add(chatRoom.getRoomId());
//                            chatRoom.getPersons().add(session);
//                            SocketChatMessage socketChatMessage = new SocketChatMessage(null, new MessageData(),"chat");
//                            socketChatMessage.getMessageData().setMessageContent(session.getAttributes().get("username").toString() + " da tham gia phong chat: " + chatRoom.getRoomId());
//                            socketChatMessage.getMessageData().setCreatedDate(Calendar.getInstance().getTime());
//                            socketChatMessage.setRoomId(chatRoom.getRoomId());
//                            WebSocketMessage mss = new TextMessage(new JSONObject(socketChatMessage).toString());
//                            session.sendMessage(mss);
//                            chatRoom.sendMessage(session, mss);
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    break;
//                case "sendChatMessage":
//                    try{
//                        if(receive.getString("topic").equals("chat")) {
//                            String roomIdc = receive.getString("roomId");
//                            ChatModel chatRoom = chatRooms.stream().filter(s -> s.getRoomId().equals(roomIdc.toString())).findFirst().get();
//                            SocketChatMessage socketChatMessage = new SocketChatMessage(null, new MessageData(),"chat");
//                            socketChatMessage.getMessageData().setMessageContent(socketMessage.getMessageData().getMessageContent());
//                            socketChatMessage.getMessageData().setCreatedDate(Calendar.getInstance().getTime());
//                            socketChatMessage.setRoomId(chatRoom.getRoomId());
//                            WebSocketMessage mss = new TextMessage(new JSONObject(socketChatMessage).toString());
//                            chatRoom.sendMessage(session, mss);
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    break;
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
        if(chatRooms.values().contains(session)){
            chatRooms.remove(customUserDetail.getUser().getId());
        }
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

//    private void subscribeTopic(WebSocketSession session, SocketMessage socketMessage, List<Object> topics, boolean subscribe) {
//        if (topics == null)
//            throw new RuntimeException("Topic is null");
//        List userTopics = (List<String>) session.getAttributes().get("topics");
//        if (subscribe) {
//            for (Object topic : topics) {
//                if (!userTopics.contains(topic)) {
//                    userTopics.add(topic);
//                }
//            }
//        } else {
//            userTopics.removeAll(topics);
//        }
//    }
//
//    private void publishMessage(SocketMessage socketMessage, WebSocketMessage<?> message) throws IOException {
//        List<WebSocketSession> sendSessions = new ArrayList<>();
//        for (Long uid : socketMessage.getUidSet()) {
//            List<String> topics = (List<String>) userSessions.get(uid).getAttributes().get("topics");
//            if (topics.contains(socketMessage.getTopic())) {
//                sendSessions.add(userSessions.get(uid));
//            }
//        }
//        System.out.println(sendSessions.toString());
//        if (sendSessions.size() > 0) {
//            for (WebSocketSession s : sendSessions
//            ) {
//                s.sendMessage(message);
//            }
//        }
//    }

    public void publishNotification(SocketNotificationModel notification, List<Long> uIds) {
        NotificationSocketMessage notificationSocketMessage = new NotificationSocketMessage("notification", notification);
        WebSocketMessage mss = new TextMessage(new JSONObject(notificationSocketMessage).toString());
        if (uIds == null)
            userSessions.forEachValue(10l, s -> this.sendMessage(s, mss));
        else uIds.forEach(uId -> this.sendMessage(userSessions.get(uId), mss));
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage<?> mss) {
        if (session != null) {
            try {
                session.sendMessage(mss);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getAllChatRoomId(WebSocketSession session) {
        List<String> roomIds = new ArrayList<>();
        chatRooms.forEach((roomId,room) -> roomIds.add(roomId));
        return roomIds;
        }

    public List<ChatModel> getAllChatRoom() {
        List<ChatModel> rooms = new ArrayList<>();
        chatRooms.forEach((roomId,room) -> rooms.add(room));
        return rooms;
    }

    public List<ChatModel> getAllAvailableChatRoom() {
        List<ChatModel> availRooms = this.chatRooms.values().stream().filter(x->x.hasPersons(1)).collect(Collectors.toList());
        return availRooms;
    }

    public List<ChatModel> getAllFullChatRoom() {
        Long uid = SecurityUtils.getCurrentUserId();
        List<ChatModel> availRooms = this.chatRooms.values().stream().filter(x->x.hasPersons(2) && x.getPersons().contains(userSessions.get(uid))).collect(Collectors.toList());
        return availRooms;
    }

    private Long getKeyFromValue(WebSocketSession session) {
        for (Long key : userSessions.keySet()) {
            if (userSessions.get(key).equals(session)) {
                return key;
            }
        }
        return null;
    }

    public static String getCurrentChatRoomId(WebSocketSession session) {
        Long uid = SecurityUtils.getCurrentUserId();
        AtomicReference<String> roomId = new AtomicReference<>(null);
        if(chatRooms.size()>0){
            chatRooms.values().stream().forEach(x->{
                if(x.getPersons().contains(userSessions.get(uid))){
                    roomId.set(x.getRoomId());
                }
            });
            return roomId.get();
        }else {
            return null;
        }

    }

}

