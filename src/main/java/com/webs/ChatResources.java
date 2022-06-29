package com.webs;


import com.config.socket.NotificationSocketMessage;
import com.config.socket.SocketHandler;
import com.dtos.ResponseDto;
import com.dtos.SocketDtos.ChatMessageDto;
import com.dtos.SocketDtos.ChatRoomDto;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.ChatMessageModel;
import com.models.sockets.ChatModel;
import com.models.sockets.MessageData;
import com.models.sockets.SocketChatMessage;
import com.services.IChatService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatResources {
    @Autowired
    SocketHandler socketHandler;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @PostMapping("/createChatRoom")
    public ResponseDto createChatRoom() {
        WebSocketSession curSession = SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId());
        if(SocketHandler.getCurrentChatRoomId(curSession)!=null){
            return ResponseDto.of(null, "You are already in a chat room : "+ SocketHandler.getCurrentChatRoomId(curSession));
        }else {
            String roomId = UUID.randomUUID().toString();
            ChatModel newChatRoom = new ChatModel(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()), roomId,false);
            SocketHandler.chatRooms.put(roomId, newChatRoom);
            return ResponseDto.of(roomId, "Create chat room");
        }
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @PostMapping("/joinChatRoom")
    public ResponseDto joinChatRoom(@PathParam("roomId") String roomId) throws IOException {
        ChatModel chatRoom = SocketHandler.chatRooms.get(roomId);
        if (chatRoom == null) {
            throw new RuntimeException("Chat room not found");
        }
        chatRoom.joinRoom(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()));
        return ResponseDto.of(roomId, "Join chat room");
    }
    @PostMapping("/sendChatMessage")
    public ResponseDto sendChatMessage(ChatMessageModel model) {
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
            ChatMessageDto chatMessageDto = new ChatMessageDto(model.getMessage(), model.getRoomId(), imageList, SecurityUtils.getCurrentUsername());
            NotificationSocketMessage socketMessage = new NotificationSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            chatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message);
            return ResponseDto.of(socketMessage, "Send chat message");
        }else {
            return ResponseDto.of(null, "Send chat message");
        }

    }
    @GetMapping("/getAllChatRoom")
    public ResponseDto getAllChatRoom() {
        return ResponseDto.of(socketHandler.getAllChatRoom().stream().map(ChatRoomDto::toDto), "Get all chat room");
    }
    @GetMapping("/getAllAvailableChatRoom")
    public ResponseDto getAllAvailableChatRoom() {
        return ResponseDto.of(socketHandler.getAllAvailableChatRoom().stream().map(ChatRoomDto::toDto), "Get all available chat room");
    }

}
