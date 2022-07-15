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
    private ConcurrentHashMap<String, WebSocketSession> persons;
    private Date createdDate;
    private Long roomId;
    private List<ChatMessageModel> messages;

    private boolean isUserJoined = false;
    private Long adminUserId;
    private boolean isAdminJoined = false;

    public ChatRoomModel(WebSocketSession session, ChatRoomEntity entity) {
        this.persons = new ConcurrentHashMap<>();
        persons.put(session.getId(), session);
        this.createdDate = Calendar.getInstance().getTime();
        this.roomId = entity.getRoomId();
        this.messages = entity.getMessages().stream().map(ChatMessageModel::toModel).collect(Collectors.toList());
    }

    public int size() {
        return this.getPersons().size();
    }

    public void sendMessage(String sessionId, WebSocketMessage<?> message) {
        persons.forEach((key, person) -> {
            if (!key.equals(sessionId)) {
                try {
                    person.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public void removeUserSession(String sessionId) {
        this.persons.remove(sessionId);
    }

}
