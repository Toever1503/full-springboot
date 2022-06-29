package com.dtos.SocketDtos;

import com.config.socket.SocketHandler;
import com.models.sockets.ChatModel;
import com.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {

    private String roomId;
    private String userName;
    private Date createdDate;
    private String userImage;

    public static ChatRoomDto toDto(ChatModel chatModel){
        return ChatRoomDto.builder()
                .roomId(chatModel.getRoomId())
                .userName(SocketHandler.getUserFromSession(chatModel.getPersons().get(0)).getUserName())
                .createdDate(chatModel.getCreatedDate())
                .userImage(SocketHandler.getUserFromSession(chatModel.getPersons().get(0)).getAvatar())
                .build();
    }
}
