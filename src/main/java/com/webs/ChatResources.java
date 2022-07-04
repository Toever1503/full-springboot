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
import org.springframework.transaction.annotation.Transactional;
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
    IChatService chatService;
    @PostMapping("/createChatRoom")
    public ResponseDto createChatRoom() {
        return ResponseDto.of(chatService.createChatRoom(),"Create chat room");
    }

    @Transactional
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @PostMapping("/joinChatRoom")
    public ResponseDto joinChatRoom(@PathParam("roomId") String roomId) throws IOException {
        return ResponseDto.of(chatService.joinChatRoom(roomId), "Join chat room");
    }

    @Transactional
    @PostMapping("/sendChatMessage")
    public ResponseDto sendChatMessage(ChatMessageModel model) {
        return ResponseDto.of(chatService.sendMessage(model), "Send chat message");
    }
    @Transactional
    @GetMapping("/getAllChatRoom")
    public ResponseDto getAllChatRoom() {
        return ResponseDto.of(chatService.getAllRoomList(), "Get all chat room");
    }
    @Transactional
    @GetMapping("/getAllAvailableChatRoom")
    public ResponseDto getAllAvailableChatRoom() {
        return ResponseDto.of(chatService.getAvailableRoomList(), "Get all available chat room");
    }

}
