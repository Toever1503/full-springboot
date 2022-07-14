package com.webs;


import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.ChatMessageModel;
import com.services.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
@RequestMapping("/chat")
public class ChatResources {
    @Autowired
    IChatService chatService;

    @RolesAllowed(RoleEntity.USER)
    @Transactional
    @GetMapping("/createChatRoom")
    public ResponseDto createChatRoom() {
        return ResponseDto.of(chatService.createChatRoom(),"Create chat room");
    }

    @Transactional
    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @GetMapping("/joinChatRoom/{roomId}")
    public ResponseDto joinChatRoom(@PathVariable Long roomId){
        return ResponseDto.of(chatService.joinChatRoom(roomId), "Join chat room");
    }

    @Transactional
    @PostMapping("/sendChatMessage")
    public ResponseDto sendChatMessage(ChatMessageModel model) {
        return ResponseDto.of(chatService.sendMessage(model), "Send chat message");
    }
    @Transactional
    @GetMapping("/getAllChatRoom")
    public ResponseDto getAllChatRoom(Pageable pageable) {
        return ResponseDto.of(chatService.getAllRoomList(pageable), "Get all chat room");
    }
    @Transactional
    @GetMapping("/getAllMyChatRoom")
    public ResponseDto getAllMyChatRoom(Pageable pageable) {
        return ResponseDto.of(chatService.getAllMyChatRoom(pageable), "Get all my chat room");
    }


    @Transactional
    @GetMapping("/getAllChatRoomMessage/{id}")
    public ResponseDto getAllChatRoomMessage(@PathVariable("id") Long id) {
        return ResponseDto.of(chatService.getAllRoomChatMessages(id), "Get all messages from room: "+id);
    }
}
