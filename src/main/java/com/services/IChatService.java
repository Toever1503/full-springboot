package com.services;

import com.models.chat_models.GeneralSocketMessage;
import com.dtos.socket_dtos.ChatRoomDto;
import com.models.ChatMessageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IChatService {
    GeneralSocketMessage sendMessage(ChatMessageModel model);

    Long createChatRoom();

    String joinChatRoom(Long roomId);

    Page<ChatRoomDto> getAllRoomList(Pageable pageable);

    List<ChatRoomDto> getAllMyChatRoom(Pageable pageable);
    List<GeneralSocketMessage> getAllRoomChatMessages(Long roomId);
}
