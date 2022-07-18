package com.services.impl;

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
        if (SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR))
            throw new RuntimeException("You are administrator, so you can't create chat room for self!");
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        WebSocketSession userSession = this.getUserSession(userEntity.getId());

        // find room on database, if room not found then create new room for user
        ChatRoomEntity chatRoomEntity = this.chatRoomRepository.findByCreatedById(userEntity.getId())
                .orElse(ChatRoomEntity.builder()
                        .messages(List.of())
                        .createdBy(userEntity).build());
        if (!chatRoomEntity.getCreatedBy().getId().equals(userEntity.getId()))
            throw new RuntimeException("You can't create chat room because this room is other user!");

        // save room to database if room not found
        if (chatRoomEntity.getRoomId() == null) this.chatRoomRepository.save(chatRoomEntity);
        ChatRoomModel chatRoomModel = userChatRooms.get(chatRoomEntity.getRoomId());
        if (chatRoomModel == null) {
            chatRoomModel = new ChatRoomModel(chatRoomEntity);
            userChatRooms.putIfAbsent(chatRoomEntity.getRoomId(), chatRoomModel);
        }
        chatRoomModel.setUserSession(userSession);

        List<Long> roomIds = (List<Long>) userSession.getAttributes().get("roomIds");
        if (!roomIds.contains(chatRoomEntity.getRoomId()))
            roomIds.add(chatRoomEntity.getRoomId());

        // add room to socket user chat rooms list
        return chatRoomEntity.getRoomId();
    }

    @Override
    public String joinChatRoom(Long roomId) {
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        WebSocketSession userSession = this.getUserSession(userEntity.getId());
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found!"));

        ChatRoomModel chatRoomModel = userChatRooms.get(chatRoom.getRoomId());
        if (chatRoomModel != null) { // if room has been created
            if (chatRoomModel.getAdminSession() == null) {
                chatRoomModel.setAdminSession(userSession);
            } else if (chatRoomModel.getAdminSession().getId().equals(userSession.getId())) {
                throw new RuntimeException("Tư vấn viên khác hiện đã tham gia!");
            }
        } else { // create new chat room
            chatRoomModel = new ChatRoomModel(chatRoom);
            userChatRooms.putIfAbsent(chatRoom.getRoomId(), chatRoomModel);
            chatRoomModel.setAdminSession(userSession);
        }

        String message = new StringBuilder().append("Tư vấn viên ").append(UserEntity.getName(userEntity)).append(" đã tham gia phòng chat!").toString();
        List<Long> roomIds = (List<Long>) userSession.getAttributes().get("roomIds"); // add room id to list room id of user
        if (!roomIds.contains(chatRoom.getRoomId()))
            roomIds.add(chatRoom.getRoomId());

        ChatMessageDto chatData = ChatMessageDto.
                builder()
                .roomId(chatRoom.getRoomId())
                .attachments(List.of())
                .message(message)
                .senderRole(RoleEntity.ADMINISTRATOR)
                .sender("Tư vấn viên ".concat(UserEntity.getName(userEntity)))
                .build();
        String jsonMss = new JSONObject(GeneralSocketMessage.builder().topic("Chat").data(chatData).build()).toString();
        chatRoomModel.sendMessage(userSession.getId(), new TextMessage(jsonMss));
        return message;
    }

    @Transactional
    @Override
    public Page<ChatRoomDto> getAllRoomList(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        WebSocketSession userSession = this.getUserSession(userId);
        return this.chatRoomRepository.findAll(pageable).map(room -> {
            ChatRoomModel chatRoomModel = userChatRooms.get(room.getRoomId());
            ChatRoomDto dto = ChatRoomDto.toDto(room);

            if (chatRoomModel != null) {
                dto.setIsUserJoined(chatRoomModel.getUserSession() == null ? false : true);
                dto.setHasOtherAdmin(chatRoomModel.getAdminSession() == null ?
                        false : (chatRoomModel.getAdminSession().getId().equals(userSession.getId()) ? false : true));
            }
            return dto;
        });
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

