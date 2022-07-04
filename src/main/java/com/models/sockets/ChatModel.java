package com.models.sockets;

import com.config.socket.exception.ChatRoomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Data
public class ChatModel {
    private List<WebSocketSession> persons;
    private Date createdDate;

    private Date updatedDate;
    private String roomId;
    private boolean isMultiple;


    public ChatModel(WebSocketSession person, String roomId, boolean isMultiple) {
        this.persons = new ArrayList<>();
        this.persons.add(person);
        this.createdDate = Calendar.getInstance().getTime();
        this.roomId = roomId;
        this.isMultiple = isMultiple;
    }

    public void sendMessage(WebSocketSession session, WebSocketMessage<?> message, boolean sendToAll) {
        this.updatedDate = Calendar.getInstance().getTime();
        if(sendToAll){
            for (WebSocketSession person : persons){
                try {
                    person.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
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
    }

    public void joinRoom(WebSocketSession session) throws ChatRoomException {
        if (this.isMultiple)
            this.persons.add(session);
        else {
            if (this.persons.size() < 2) {
                this.persons.add(session);
            }else {
                throw new ChatRoomException("chat", this.roomId, "Room is full");
            }
        }
    }

    public boolean hasPersons(int size) {
        return this.persons.size() == size;
    }

}
