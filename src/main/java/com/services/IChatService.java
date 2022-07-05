package com.services;

import com.models.socket_models.GeneralSocketMessage;
import com.dtos.socket_dtos.ChatRoomDto;
import com.models.ChatMessageModel;

import java.util.List;

public interface IChatService {
    GeneralSocketMessage sendMessage(ChatMessageModel model);

    String createChatRoom();

    String joinChatRoom(String roomId);

    List<ChatRoomDto> getAllRoomList();

    List<ChatRoomDto> getAvailableRoomList();
    List<ChatRoomDto> getAllMyChatRoom();
    List<ChatRoomDto> getAllUserChatRoom();
    List<GeneralSocketMessage> getAllRoomChatMessages(String roomId);
}
