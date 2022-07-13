package com.models.socket_models;

import com.config.socket.SocketHandler;
import com.config.socket.exception.ChatRoomException;
import com.entities.ChatRoomEntity;
import com.utils.SecurityUtils;
import lombok.*;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
public class ChatRoomModel {
    private List<WebSocketSession> persons;
    private Date createdDate;
    private Date updatedDate;
    private String roomId;
    private boolean isMultiple;
    private List<GeneralSocketMessage> messages;

    public ChatRoomModel(){
        this.persons = new ArrayList<>();
    }

    public void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        this.updatedDate = Calendar.getInstance().getTime();
            persons.stream().filter(x->!x.equals(session)).forEach(x->{
                try {
                    x.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public boolean hasPersons(int size) {
        if(this.persons==null){
            return false;
        }
        return this.persons.size() == size;
    }

    public static  ChatRoomModel toModel(ChatRoomEntity entity){
        ChatRoomModel chatRoomModel = ChatRoomModel.builder().roomId(entity.getRoomId().toString()).isMultiple(false).messages(entity.getMessageEntities().stream().map(GeneralSocketMessage::toGeneralSocketMessage).collect(Collectors.toList())).persons(new ArrayList<>()).build();
        return chatRoomModel;
    }

    public List<GeneralSocketMessage> getMessages() {
        return messages;
    }

}
