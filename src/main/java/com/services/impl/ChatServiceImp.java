package com.services.impl;

import com.models.socket_models.GeneralSocketMessage;
import com.config.socket.SocketHandler;
import com.dtos.socket_dtos.ChatMessageDto;
import com.dtos.socket_dtos.ChatRoomDto;
import com.entities.RoleEntity;
import com.models.ChatMessageModel;
import com.models.socket_models.ChatRoomModel;
import com.models.socket_models.SocketChatUserModel;
import com.services.IChatService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImp implements IChatService {
    @Autowired
    SocketHandler socketHandler;
    @Autowired
    FileUploadProvider fileUploadProvider;

    @Override
    public GeneralSocketMessage sendMessage(ChatMessageModel model) {
        if(SocketHandler.chatRooms.get(model.getRoomId())!=null){
            ChatRoomModel chatRoom = SocketHandler.chatRooms.get(model.getRoomId());
            List<String> imageList = new ArrayList<>();
            if(model.getAttachments()!=null){
                model.getAttachments().stream().forEach(x->{
                    try {
                        String image = fileUploadProvider.uploadFile("chat/" + SecurityUtils.getCurrentUser().getUsername()+"/", x);
                        imageList.add(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            if(SecurityUtils.getCurrentUser().getUser().getRoleEntity().stream().anyMatch(x->x.getRoleName().equals(RoleEntity.ADMINISTRATOR))){
                chatMessageDto = new ChatMessageDto(model.getMessage(), model.getRoomId(), imageList, SecurityUtils.getCurrentUsername(),"ADMINISTRATOR");
            }else {
                chatMessageDto = new ChatMessageDto(model.getMessage(), model.getRoomId(), imageList, SecurityUtils.getCurrentUsername(),"USER");
            }
            GeneralSocketMessage socketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            chatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message, false);
            if(chatRoom.getMessages()==null){
                chatRoom.setMessages(new ArrayList<>());
            }
            chatRoom.getMessages().add(socketMessage);
            return socketMessage;
        }else
        {
            return null;
        }
    }

    @Override
    public String createChatRoom() {
        WebSocketSession curSession = SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId());
        if (SocketHandler.getCurrentChatRoomId(curSession) == null)
        {
            String roomId = UUID.randomUUID().toString();
            ChatRoomModel newChatRoom = new ChatRoomModel(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()), roomId, false, null);
            SocketHandler.chatRooms.put(roomId, newChatRoom);
            ChatMessageDto chatMessageDto = new ChatMessageDto("Nguoi dung "+SecurityUtils.getCurrentUsername()+" da tao phong chat:"+roomId , roomId,null , SecurityUtils.getCurrentUsername(),RoleEntity.USER);
            GeneralSocketMessage socketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            newChatRoom.sendMessage(curSession,message,true);
            newChatRoom.setMessages(new ArrayList<>());
            newChatRoom.getMessages().add(socketMessage);
            //get current chat rooms and add new one
            if(!SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).findAny().isPresent())
            {
                SocketHandler.userChatRooms.add(new SocketChatUserModel(SecurityUtils.getCurrentUserId(),new ArrayList<>()));
            }
            SocketHandler.userChatRooms.stream().filter(x->x.getUserId().equals(SecurityUtils.getCurrentUserId())).forEach(x->{
                x.getChatRoomList().add(newChatRoom);
            });
            return roomId;
        }
            return SocketHandler.getCurrentChatRoomId(curSession);
    }

    @Override
    public String joinChatRoom(String roomId) {
        ChatRoomModel chatRoom = SocketHandler.chatRooms.get(roomId);
        if (chatRoom == null) {
            throw new RuntimeException("Chat room not found");
        }
        if(chatRoom.hasPersons(1)){
            ChatMessageDto chatMessageDto = new ChatMessageDto("[ADMIN] "+SecurityUtils.getCurrentUsername()+" da tham gia phong chat:"+roomId , roomId,null , SecurityUtils.getCurrentUsername(), RoleEntity.ADMINISTRATOR);
            GeneralSocketMessage socketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            chatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message,true);
            chatRoom.getMessages().add(socketMessage);
        }
        return chatRoom.joinRoom(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()));
    }

    @Override
    public List<ChatRoomDto> getAllRoomList() {
        return socketHandler.getAllChatRoom().stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getAvailableRoomList() {
        return socketHandler.getAllAvailableChatRoom().stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getAllMyChatRoom() {
        return socketHandler.getAllMyChatRoom(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId())).stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getAllUserChatRoom() {
        return socketHandler.getAllUserChatRoom(SecurityUtils.getCurrentUserId()).stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<GeneralSocketMessage> getAllRoomChatMessages(String roomId) {
        return SocketHandler.chatRooms.get(roomId).getMessages();
    }
}

