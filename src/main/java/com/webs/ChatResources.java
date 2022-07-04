package com.webs;


import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.ChatMessageModel;
import com.services.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.websocket.server.PathParam;
import java.io.IOException;

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
    @GetMapping("/getAllMyChatRoom")
    public ResponseDto getAllAvailableChatRoom() {
        return ResponseDto.of(chatService.getAllMyChatRoom(), "Get all my chat room");
    }

}
