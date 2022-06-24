package com.models.sockets;

import com.config.socket.exception.ChatRoomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Data
public class ChatModel {
    private List<WebSocketSession> persons;
    private Date createdDate;
    private String roomId;
    private boolean isMultiple;

    public ChatModel(WebSocketSession person, String roomId, boolean isMultiple) {
        this.persons = new ArrayList<>();
        this.persons.add(person);
        this.createdDate = Calendar.getInstance().getTime();
        this.isMultiple = isMultiple;
    }

    public void sendMessage(WebSocketSession session, WebSocketMessage<TextMessage> message) {
        for (WebSocketSession person : persons) {
            if (person.getId().equals(session.getId()))
                continue;
            try {
                person.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void joinRoom(WebSocketSession session) throws ChatRoomException {
        if (this.isMultiple)
            this.persons.add(session);
        else {
            if (this.persons.size() < 2) {
                this.persons.add(session);
            }
            throw new ChatRoomException("chat", this.roomId, "Room is full");
        }
    }

}
