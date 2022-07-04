package com.services;

import com.config.socket.NotificationSocketMessage;
import com.dtos.SocketDtos.ChatRoomDto;
import com.models.ChatMessageModel;
import com.models.sockets.SocketChatMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IChatService {
    NotificationSocketMessage sendMessage(ChatMessageModel model);

    String createChatRoom();

    String joinChatRoom(String roomId);

    List<ChatRoomDto> getAllRoomList();

    List<ChatRoomDto> getAvailableRoomList();

}
