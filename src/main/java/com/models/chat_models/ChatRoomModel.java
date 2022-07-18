package com.models.chat_models;

import com.config.socket.SocketHandler;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.entities.chat.ChatRoomEntity;
import lombok.*;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomModel {
    private Date createdDate;
    private Long roomId;
    private List<ChatMessageModel> messages;

    private WebSocketSession userSession;
    private WebSocketSession adminSession;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomInfo {
        private Long roomId;
        private String userName;
        private String adminName;
    }


    public ChatRoomModel(ChatRoomEntity entity) {
        this.createdDate = entity.getCreatedDate();
        this.roomId = entity.getRoomId();
        this.messages = entity.getMessages().stream().map(ChatMessageModel::toModel).collect(Collectors.toList());
    }


    public void sendMessage(String sessionId, WebSocketMessage<?> message) {
        if (sessionId.equals(adminSession.getId()))
            this.sendMessage(this.userSession, message); // admin send to user
        else
            this.sendMessage(this.adminSession, message); // user send to admin
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (session != null)
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public void removeUserSession(String sessionId) {
        if (sessionId.equals(userSession.getId()))
            this.userSession = null;
        else
            this.adminSession = null;
    }
}
