package com.services;

import com.dtos.SocketDtos.ChatRoomDto;
import com.models.sockets.SocketChatMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IChatService {
    SocketChatMessage sendMessage(SocketChatMessage socketChatMessage);

    boolean createChatRoom(SocketChatMessage socketChatMessage);

    boolean joinChatRoom(SocketChatMessage socketChatMessage);

    List<ChatRoomDto> getAllRoomList();

    List<ChatRoomDto> getAvailableRoomList();

    List<ChatRoomDto> getFullRoomList();
}
