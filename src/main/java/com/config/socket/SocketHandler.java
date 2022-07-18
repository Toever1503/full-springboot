package com.config.socket;

import com.config.socket.exception.ChatRoomException;
import com.dtos.socket_dtos.ChatMessageDto;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.google.gson.Gson;
import com.models.ChatMessageModel;
import com.models.SocketNotificationModel;
import com.models.chat_models.*;
import com.services.CustomUserDetail;
import com.services.IChatService;
import com.services.impl.ChatServiceImp;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketHandler implements WebSocketHandler {

    private final IChatService chatService;
    public static ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public SocketHandler(IChatService chatService) {
        this.chatService = chatService;
    }

    @Transactional
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.getAttributes().put("topics", new ArrayList<>());
        session.getAttributes().put("roomIds", new ArrayList<>());
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        session.getAttributes().put("username", customUserDetail.getUsername());
        session.getAttributes().put("id", customUserDetail.getUser().getId());
        userSessions.put(customUserDetail.getUser().getId(), session);
    }

    public static UserEntity getUserFromSession(WebSocketSession session) {
        return ((CustomUserDetail) ((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal()).getUser();
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
        socketMessage.setUidSet(userSessions.keySet());
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        userSessions.remove(customUserDetail.getUser().getId());
        String role = UserEntity.hasRole(RoleEntity.ADMINISTRATOR, customUserDetail.getUser().getRoleEntity()) ? RoleEntity.ADMINISTRATOR : RoleEntity.USER;
        String name = UserEntity.getName(customUserDetail.getUser());

        List<Long> roomIds = (List<Long>) this.getValueFromSessionAttribute(session, "roomIds");
        if (roomIds != null)
            roomIds.forEach(roomId -> {
                ChatRoomModel chatRoom = ChatServiceImp.userChatRooms.get(roomId);
                if (chatRoom != null) {
//                    GeneralSocketMessage mssData = GeneralSocketMessage.builder()
//                            .topic("Chat")
//                            .data(ChatMessageDto.builder()
//                                    .id(Math.round(Math.random()*10))
//                                    .sender(name)
//                                    .senderRole(role)
//                                    .message((role.equals(RoleEntity.ADMINISTRATOR) ? "Tư vấn viên " : "")
//                                            .concat(name.concat(" đã rời phòng chát!")))
//                                    .build())
//                            .build();
                    ChatMessageModel chatMessageModel = ChatMessageModel
                            .builder()
                            .attachments(null)
                            .message((role.equals(RoleEntity.ADMINISTRATOR) ? "Tư vấn viên " : "")
                                    .concat(name.concat(" đã rời phòng chát!")))
                            .roomId(roomId)
                            .build();
                    this.chatService.sendMessage(chatMessageModel);
//                    chatRoom.sendMessage(session.getId(), new TextMessage(new JSONObject(mssData).toString()));
                    chatRoom.removeUserSession(session.getId());
                }
            });
    }

    private Object getValueFromSessionAttribute(WebSocketSession session, String key) {
        return session.getAttributes().get(key);
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


    public void publishNotification(SocketNotificationModel notification, List<Long> uIds) {
        GeneralSocketMessage generalSocketMessage = new GeneralSocketMessage("notification", notification);
        WebSocketMessage mss = new TextMessage(new JSONObject(generalSocketMessage).toString());
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

    private Long getKeyFromValue(WebSocketSession session) {
        for (Long key : userSessions.keySet()) {
            if (userSessions.get(key).equals(session)) {
                return key;
            }
        }
        return null;
    }


}

