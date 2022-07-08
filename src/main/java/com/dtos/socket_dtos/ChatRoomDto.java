package com.dtos.socket_dtos;

import com.config.socket.SocketHandler;
import com.entities.ChatRoomEntity;
import com.entities.RoleEntity;
import com.entities.UserEntity;
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

    public static ChatRoomDto toDto(ChatRoomEntity chatRoomEntity){
        UserEntity roomOwner = new UserEntity();
        if(chatRoomEntity.getUserEntities().stream().anyMatch(x->x.getRoleEntity().size()==1 && x.getRoleEntity().stream().findFirst().get().getRoleName().equals(RoleEntity.USER))){
            roomOwner = chatRoomEntity.getUserEntities().stream().filter(x->x.getRoleEntity().size()==1 && x.getRoleEntity().stream().findFirst().get().getRoleName().equals(RoleEntity.USER)).findFirst().get();
        }
        return ChatRoomDto.builder()
                .roomId(chatRoomEntity.getRoomId())
                .userName(roomOwner.getUserName())
                .createdDate(chatRoomEntity.getCreatedDate())
                .userImage(roomOwner.getAvatar())
                .build();
    }
}
