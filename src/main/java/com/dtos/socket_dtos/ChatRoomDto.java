package com.dtos.socket_dtos;

import com.config.socket.SocketHandler;
import com.models.socket_models.ChatRoomModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static ChatRoomDto toDto(ChatRoomModel chatRoomModel){
        return ChatRoomDto.builder()
                .roomId(chatRoomModel.getRoomId())
                .userName(SocketHandler.getUserFromSession(chatRoomModel.getPersons().get(0)).getUserName())
                .createdDate(chatRoomModel.getCreatedDate())
                .userImage(SocketHandler.getUserFromSession(chatRoomModel.getPersons().get(0)).getAvatar())
                .build();
    }
}
