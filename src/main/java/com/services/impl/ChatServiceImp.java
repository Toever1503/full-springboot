package com.services.impl;

import com.config.socket.NotificationSocketMessage;
import com.config.socket.SocketHandler;
import com.dtos.ResponseDto;
import com.dtos.SocketDtos.ChatMessageDto;
import com.dtos.SocketDtos.ChatRoomDto;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.ChatMessageModel;
import com.models.sockets.ChatModel;
import com.models.sockets.SocketChatMessage;
import com.services.IChatService;
import com.services.IUserService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
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
    public NotificationSocketMessage sendMessage(ChatMessageModel model) {
        if(SocketHandler.chatRooms.get(model.getRoomId())!=null){
            ChatModel chatRoom = SocketHandler.chatRooms.get(model.getRoomId());
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
            NotificationSocketMessage socketMessage = new NotificationSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            chatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message, false);
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
            ChatModel newChatRoom = new ChatModel(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()), roomId, false);
            SocketHandler.chatRooms.put(roomId, newChatRoom);
            ChatMessageDto chatMessageDto = new ChatMessageDto("Nguoi dung "+SecurityUtils.getCurrentUsername()+" da tao phong chat:"+roomId , roomId,null , SecurityUtils.getCurrentUsername(),RoleEntity.USER);
            NotificationSocketMessage socketMessage = new NotificationSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            newChatRoom.sendMessage(curSession,message,true);
            return roomId;
        }
            return SocketHandler.getCurrentChatRoomId(curSession);
    }

    @Override
    public String joinChatRoom(String roomId) {
        ChatModel chatRoom = SocketHandler.chatRooms.get(roomId);
        if (chatRoom == null) {
            throw new RuntimeException("Chat room not found");
        }
        ChatMessageDto chatMessageDto = new ChatMessageDto("[ADMIN] "+SecurityUtils.getCurrentUsername()+" da tham gia phong chat:"+roomId , roomId,null , SecurityUtils.getCurrentUsername(), RoleEntity.ADMINISTRATOR);
        NotificationSocketMessage socketMessage = new NotificationSocketMessage("Chat",chatMessageDto);
        WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
        chatRoom.joinRoom(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()));
        chatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message,true);
        return roomId;
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
}

