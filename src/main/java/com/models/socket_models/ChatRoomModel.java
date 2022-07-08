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


//    public ChatRoomModel(WebSocketSession person, String roomId, boolean isMultiple, List<GeneralSocketMessage> messages) {
//        this.persons = new ArrayList<>();
//        this.persons.add(person);
//        this.createdDate = Calendar.getInstance().getTime();
//        this.roomId = roomId;
//        this.isMultiple = isMultiple;
//        this.messages = messages;
//    }


//    public ChatRoomModel(WebSocketSession person, Date createdDate, Date updatedDate, String roomId, boolean isMultiple, List<GeneralSocketMessage> messages) {
//        this.persons = new ArrayList<>();
//        this.persons.add(person);
//        this.createdDate = Calendar.getInstance().getTime();
//        this.roomId = roomId;
//        this.isMultiple = isMultiple;
//        this.messages = messages;
//    }

    public ChatRoomModel(){
        this.persons = new ArrayList<>();
    }

    public void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        this.updatedDate = Calendar.getInstance().getTime();
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

    public String joinRoom(WebSocketSession session) throws ChatRoomException {
//        if (this.isMultiple){
//            this.persons.add(session);
//            if(!SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).findAny().isPresent())
//            {
//                SocketHandler.userChatRooms.add(new SocketChatUserModel(SecurityUtils.getCurrentUserId(),new ArrayList<>()));
//            }
//            SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).forEach(x->{
//                x.getChatRoomList().add(this);
//            });
//            return this.roomId;
//        }
//        else {
//            if (this.persons.size() < 2) {
//                this.persons.add(session);
//                if(!SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).findAny().isPresent())
//                {
//                    SocketHandler.userChatRooms.add(new SocketChatUserModel(SecurityUtils.getCurrentUserId(),new ArrayList<>()));
//                }
//                SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).forEach(x->{
//                    x.getChatRoomList().add(this);
//                });
//                return this.roomId;
//            } else if (this.persons.contains(session)) {
//                return this.roomId;
//            }
//            else {
//                throw new RuntimeException("Room is full");
//            }
//        }
        return null;
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
