package com.config.socket;

import com.config.socket.exception.ChatRoomException;
import com.entities.ChatRoomEntity;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.google.gson.Gson;
import com.models.SocketNotificationModel;
import com.models.socket_models.*;
import com.repositories.IChatRoomRepository;
import com.services.CustomUserDetail;
import com.utils.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class SocketHandler implements WebSocketHandler {
    @Autowired
    IChatRoomRepository chatRoomRepository;
    public static ConcurrentHashMap<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    public static List<ChatRoomModel> userChatRooms = new ArrayList<>();

    @Transactional
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.getAttributes().put("topics", new ArrayList<>());
        UsernamePasswordAuthenticationToken userDetail = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetail.getPrincipal();
        session.getAttributes().put("username", customUserDetail.getUsername());
        session.getAttributes().put("id", customUserDetail.getUser().getId());
        userSessions.put(customUserDetail.getUser().getId(), session);
        if(chatRoomRepository.findAllByUserEntitiesContains(customUserDetail.getUser()).size() > 0){
            for (ChatRoomEntity chatRoomEntity : chatRoomRepository.findAllByUserEntitiesContains(customUserDetail.getUser())) {
                ChatRoomModel chatRoomModel = ChatRoomModel.toModel(chatRoomEntity);
                for (UserEntity userEntity : chatRoomEntity.getUserEntities()) {
                     if(userSessions.get(userEntity.getId())!=null){
                         chatRoomModel.getPersons().add(userSessions.get(userEntity.getId()));
                     }
                }
                if(chatRoomModel.getPersons().stream().filter(x->x.getAttributes().get("id").equals(customUserDetail.getUser().getId())).findAny().isPresent()){
                    chatRoomModel.getPersons().remove(chatRoomModel.getPersons().stream().filter(x->x.getAttributes().get("id").equals(customUserDetail.getUser().getId())).findAny().get());
                    chatRoomModel.getPersons().add(session);
                }
                updateUsersChatRoom(chatRoomModel);
                userChatRooms.add(chatRoomModel);
            }
        }
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
        socketMessage.setUidSet(userSessions.keySet());
        Long curUid = Long.valueOf(session.getAttributes().get("id").toString());
        System.out.println(socketMessage.toString());
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
//        if(chatRooms.values().stream().anyMatch(s -> s.getPersons().contains(session))){
//            ChatRoomModel chatRoom = chatRooms.values().stream().filter(s -> s.getPersons().contains(session)).findFirst().get();
//            chatRoom.getPersons().remove(session);
//            if(userDetail.getAuthorities().stream().map(s->s.getAuthority()).collect(Collectors.toList()).containsAll(List.of(RoleEntity.ADMINISTRATOR,RoleEntity.USER))){
//                chatRoom.sendMessage(session, new TextMessage("User "+ getUserFromSession(session).getUserName()+" has disconnected from chat room: " + chatRoom.getRoomId()),false);
//            }else {
//                chatRoom.sendMessage(session, new TextMessage("User "+ getUserFromSession(session).getUserName()+ " has closed chat room: " + chatRoom.getRoomId()),false);
//                chatRooms.remove(chatRoom.getRoomId());
//            }
//        }
        userSessions.remove(customUserDetail.getUser().getId());
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

    private static void updateUsersChatRoom(ChatRoomModel chatRoomModel){
        userChatRooms.stream().filter(x->x.getRoomId().equals(chatRoomModel.getRoomId()))
                .collect(Collectors.toList())
                .forEach(x->userChatRooms.remove(x));
    }


}

