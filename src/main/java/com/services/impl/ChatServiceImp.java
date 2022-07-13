package com.services.impl;

import com.entities.ChatRoomEntity;
import com.entities.MessageEntity;
import com.models.socket_models.GeneralSocketMessage;
import com.config.socket.SocketHandler;
import com.dtos.socket_dtos.ChatMessageDto;
import com.dtos.socket_dtos.ChatRoomDto;
import com.entities.RoleEntity;
import com.models.ChatMessageModel;
import com.models.socket_models.ChatRoomModel;
import com.repositories.IChatRoomRepository;
import com.repositories.IMessageRepository;
import com.services.IChatService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
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

    @Override
    public GeneralSocketMessage sendMessage(ChatMessageModel model) {
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(model.getRoomId()).orElseThrow(()-> new RuntimeException("Chat room not found!!!"));
        ChatRoomModel chatRoomModel = SocketHandler.userChatRooms.stream().filter(x->x.getRoomId().equals(model.getRoomId())).findFirst().orElseThrow(()-> new RuntimeException("Chat room not found!!!"));
        List<String> imageList = new ArrayList<>();
        String images = null;
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
            if(imageList.size()>0){
                images = String.join(",", imageList);
            }
            ChatMessageDto chatMessageDto = new ChatMessageDto();
            if(SecurityUtils.getCurrentUser().getUser().getRoleEntity().stream().anyMatch(x->x.getRoleName().equals(RoleEntity.ADMINISTRATOR))){
                chatMessageDto = new ChatMessageDto(model.getMessage(), model.getRoomId(), imageList, SecurityUtils.getCurrentUsername(),"ADMINISTRATOR");
            }else {
                chatMessageDto = new ChatMessageDto(model.getMessage(), model.getRoomId(), imageList, SecurityUtils.getCurrentUsername(),"USER");
            }
            GeneralSocketMessage socketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            chatRoomModel.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message);

            MessageEntity messageEntity = MessageEntity.builder()
                    .message(model.getMessage())
                    .chatRoomEntity(chatRoomEntity)
                    .user(SecurityUtils.getCurrentUser().getUser())
                    .attachment(images)
                    .build();
            chatRoomEntity.getMessageEntities().add(messageEntity);
            chatRoomRepository.save(chatRoomEntity);
            return socketMessage;
    }

    @Override
    public String createChatRoom() {
        List<ChatRoomModel> curChatRooms = SocketHandler.userChatRooms;
        if(curChatRooms.stream().noneMatch(x->x.getPersons().contains(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId())))){
            ChatRoomModel chatRoomModel = new ChatRoomModel();
            chatRoomModel.setRoomId(UUID.randomUUID().toString());
            chatRoomModel.setCreatedDate(Date.from(Instant.now()));
            chatRoomModel.setMultiple(false);
            chatRoomModel.setMessages(new ArrayList<>());
            chatRoomModel.getPersons().add(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()));
            curChatRooms.add(chatRoomModel);
            SocketHandler.userChatRooms.addAll(curChatRooms);
            ChatMessageDto chatMessageDto = new ChatMessageDto("Chat Room Created",chatRoomModel.getRoomId(),null,SecurityUtils.getCurrentUsername(),RoleEntity.USER);
            GeneralSocketMessage generalSocketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(generalSocketMessage).toString());
            chatRoomModel.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(chatMessageDto.getMessage());
            messageEntity.setUser(SecurityUtils.getCurrentUser().getUser());
            messageEntity.setAttachment(String.valueOf(chatMessageDto.getAttachments()));
            ChatRoomEntity chatRoomEntity = new ChatRoomEntity(chatRoomModel.getRoomId(),new ArrayList<>(),Set.of(SecurityUtils.getCurrentUser().getUser()),null);
            messageEntity.setChatRoomEntity(chatRoomEntity);
            chatRoomEntity.getMessageEntities().add(messageEntity);
            chatRoomRepository.save(chatRoomEntity);
            return chatRoomModel.getRoomId();
        }
        else {
            return curChatRooms.stream().filter(x->x.getPersons().contains(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()))).findFirst().get().getRoomId();
        }
    }

    @Override
    public String joinChatRoom(String roomId) {

        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        if(chatRoom.getUserEntities().size()==2 && !chatRoom.getUserEntities().stream().anyMatch(x->x.getId().equals(SecurityUtils.getCurrentUser().getUser().getId()))){
            throw new RuntimeException("Chat room is full");
        }
        if(chatRoom==null){
            throw new RuntimeException("Chat room not found");
        }

        Long chatCreatorId = chatRoom.getUserEntities().stream().findAny().get().getId();
        List<ChatRoomModel> currentuserChatRooms = SocketHandler.userChatRooms;
        if(currentuserChatRooms.size()==0){
            currentuserChatRooms = new ArrayList<>();
        }
        List<ChatRoomModel> userChatRooms = SocketHandler.userChatRooms.stream().filter(x->x.getRoomId().equals(roomId)).collect(Collectors.toList());
        if(userChatRooms.stream().anyMatch(x->x.hasPersons(1) && x.getRoomId().equals(roomId))){
            ChatRoomModel curChatRoom = userChatRooms.stream().filter(x->x.hasPersons(1) && x.getRoomId().equals(roomId)).findFirst().get();
            curChatRoom.getPersons().add(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()));
            currentuserChatRooms.add(curChatRoom);
            if(chatRoom.getUserEntities().stream().anyMatch(x->x.getId().equals(SecurityUtils.getCurrentUserId()))){
                SocketHandler.userChatRooms.addAll(currentuserChatRooms);
                return roomId;
            }

            SocketHandler.userChatRooms.addAll(currentuserChatRooms);
            ChatMessageDto chatMessageDto = new ChatMessageDto("[ADMIN] "+SecurityUtils.getCurrentUsername()+" da tham gia phong chat:"+roomId , roomId,null , SecurityUtils.getCurrentUsername(), RoleEntity.ADMINISTRATOR);
            GeneralSocketMessage socketMessage = new GeneralSocketMessage("Chat",chatMessageDto);
            WebSocketMessage message = new TextMessage(new JSONObject(socketMessage).toString());
            curChatRoom.sendMessage(SocketHandler.userSessions.get(SecurityUtils.getCurrentUserId()),message);
            MessageEntity messageEntity = MessageEntity.builder().message(chatMessageDto.getMessage()).user(SecurityUtils.getCurrentUser().getUser()).attachment(String.valueOf(chatMessageDto.getAttachments())).chatRoomEntity(chatRoom).build();
            chatRoom.getUserEntities().add(SecurityUtils.getCurrentUser().getUser());
            chatRoom.getMessageEntities().add(messageEntity);
            chatRoomRepository.save(chatRoom);
        }
        return roomId;
    }

    @Override
    public List<ChatRoomDto> getAllRoomList(Pageable pageable) {
        return chatRoomRepository.findAll(pageable).stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getAvailableRoomList(Pageable pageable) {
        return chatRoomRepository.getAllAvailableRoom(pageable).stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getAllMyChatRoom(Pageable pageable) {
        return chatRoomRepository.getAllByUserEntitiesContains(SecurityUtils.getCurrentUser().getUser(),pageable).stream().map(ChatRoomDto::toDto).collect(Collectors.toList());
    }

    @Override
    public List<GeneralSocketMessage> getAllRoomChatMessages(String roomId, Pageable pageable) {
        return messageRepository.findAllByChatRoomEntity_RoomId(roomId,pageable).stream().map(GeneralSocketMessage::toGeneralSocketMessage).collect(Collectors.toList());
    }
}

