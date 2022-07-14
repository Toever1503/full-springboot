package com.services.impl;

import com.entities.CategoryEntity;
import com.entities.UserEntity;
import com.entities.chat.ChatMessageEntity;
import com.entities.chat.ChatRoomEntity;
import com.models.chat_models.GeneralSocketMessage;
import com.config.socket.SocketHandler;
import com.dtos.socket_dtos.ChatMessageDto;
import com.dtos.socket_dtos.ChatRoomDto;
import com.entities.RoleEntity;
import com.models.ChatMessageModel;
import com.models.chat_models.ChatRoomModel;
import com.repositories.IChatRoomRepository;
import com.repositories.IMessageRepository;
import com.services.IChatService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class ChatServiceImp implements IChatService {
    @Autowired
    SocketHandler socketHandler;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IMessageRepository messageRepository;
    @Autowired
    IChatRoomRepository chatRoomRepository;

    @Autowired
    Executor taskExecutor;

    public static ConcurrentHashMap<Long, ChatRoomModel> userChatRooms = new ConcurrentHashMap<>();


    private WebSocketSession getUserSession(Long userId) {
        WebSocketSession userSession = SocketHandler.userSessions.get(userId); // check if user is opened socket

        if (userSession == null) // user must have socket to handle next step
            throw new RuntimeException("Make sure you has been opened message!");

        return userSession;
    }

    @Override
    public GeneralSocketMessage sendMessage(ChatMessageModel model) {
        WebSocketSession userSession = this.getUserSession(SecurityUtils.getCurrentUserId());
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(model.getRoomId()).orElseThrow(() -> new RuntimeException("Chat room not found!!!"));

        ChatMessageEntity chatMessageEntity = ChatMessageModel.toEntity(model, chatRoomEntity);

        List<String> uploadedFiles = new ArrayList<>();
        try {
            if (model.getAttachments() != null) {
                final String folder = new StringBuilder().append(UserEntity.FOLDER)
                        .append(SecurityUtils.getCurrentUsername())
                        .append(ChatRoomEntity.FOLDER).toString();
                try {
                    CompletableFuture.allOf(this.fileUploadProvider.asyncUploadFiles(uploadedFiles, folder, model.getAttachments())).get();

                    if (!uploadedFiles.isEmpty()) {
                        Map<String, Object> f = new HashMap<>();
                        f.put("files", uploadedFiles);
                        chatMessageEntity.setAttachment(new JSONObject(f).toString());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                        chatRoomEntity.getMessages().add(chatMessageEntity);
                    }, this.taskExecutor),
                    CompletableFuture.runAsync(() -> {
                        this.messageRepository.save(chatMessageEntity);
                    }, this.taskExecutor)).get();

            // send message to all users in chat room
            GeneralSocketMessage generalSocketMessage = GeneralSocketMessage.toGeneralSocketMessage(chatMessageEntity);
            ChatRoomModel chatRoomModel = userChatRooms.get(chatRoomEntity.getRoomId());
            if (chatRoomModel != null) {
                WebSocketMessage mss = new TextMessage(new JSONObject(generalSocketMessage).toString());
                chatRoomModel.sendMessage(userSession.getId(), mss);
            }
            return generalSocketMessage;
        } catch (Exception e) {
            taskExecutor.execute(() -> {
                if (!uploadedFiles.isEmpty())
                    uploadedFiles.forEach(this.fileUploadProvider::deleteFile);
            });
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long createChatRoom() {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        WebSocketSession userSession = this.getUserSession(userEntity.getId());

        // find room on database, if room not found then create new room for user
        ChatRoomEntity room = this.chatRoomRepository.findByCreatedById(userEntity.getId())
                .orElse(ChatRoomEntity.builder()
                        .messages(List.of())
                        .createdBy(userEntity).build());

        // save room to database if room not found
        if (room.getRoomId() == null) this.chatRoomRepository.save(room);

        // add room to socket user chat rooms list
        ChatRoomModel socketChatRoom = userChatRooms.get(room.getRoomId());
        if (socketChatRoom == null) {
            socketChatRoom = new ChatRoomModel(userSession, room);
            userChatRooms.put(room.getRoomId(), socketChatRoom);
        }
        return room.getRoomId();
    }

    @Override
    public String joinChatRoom(Long roomId) {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        WebSocketSession userSession = this.getUserSession(userEntity.getId());
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found!"));

        ChatRoomModel socketChatRoom = userChatRooms.get(chatRoom.getRoomId());
        if (socketChatRoom == null)
            throw new RuntimeException("Chat room has not been created yet!");
        socketChatRoom.putIfAbsent(userSession);

        return new StringBuilder().append("Tư vấn viên ").append(UserEntity.getName(userEntity)).append(" đã tham gia phòng chat!").toString();
    }

    @Transactional
    @Override
    public Page<ChatRoomDto> getAllRoomList(Pageable pageable) {
        return this.chatRoomRepository.findAll(pageable).map(room -> {
            ChatRoomModel chatRoomModel = userChatRooms.get(room.getRoomId());
            return ChatRoomDto.toDto(room, chatRoomModel == null ? false : (chatRoomModel.getPersons().size() >= 2 ? true : false));
        });
    }

    @Override
    public List<ChatRoomDto> getAvailableRoomList(Pageable pageable) {
        return null;
    }

    @Override
    public List<ChatRoomDto> getAllMyChatRoom(Pageable pageable) {
        return null;
    }

    @Override
    public List<GeneralSocketMessage> getAllRoomChatMessages(Long roomId) {
        ChatRoomEntity roomEntity = this.chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found!"));
        return roomEntity.getMessages().stream().map(GeneralSocketMessage::toGeneralSocketMessage).collect(Collectors.toList());
    }

}

