package com.services;

import com.models.socket_models.GeneralSocketMessage;
import com.dtos.socket_dtos.ChatRoomDto;
import com.models.ChatMessageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IChatService {
    GeneralSocketMessage sendMessage(ChatMessageModel model);

    String createChatRoom();

    String joinChatRoom(String roomId);

    List<ChatRoomDto> getAllRoomList(Pageable pageable);

    List<ChatRoomDto> getAvailableRoomList(Pageable pageable);
    List<ChatRoomDto> getAllMyChatRoom(Pageable pageable);
    List<GeneralSocketMessage> getAllRoomChatMessages(String roomId, Pageable pageable);
}
