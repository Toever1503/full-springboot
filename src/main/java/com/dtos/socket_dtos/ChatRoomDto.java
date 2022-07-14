package com.dtos.socket_dtos;

import com.entities.chat.ChatRoomEntity;
import com.entities.RoleEntity;
import com.entities.UserEntity;
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
    private Long roomId;
    private String userName;
    private String userImage;

    private Date createdDate;
    private Date updatedDate;
    private Boolean isFull;

    public static ChatRoomDto toDto(ChatRoomEntity chatRoomEntity, boolean isFull){
        return ChatRoomDto.builder()
                .roomId(chatRoomEntity.getRoomId())
                .userName(UserEntity.getName(chatRoomEntity.getCreatedBy()))
                .userImage(chatRoomEntity.getCreatedBy().getAvatar())
                .createdDate(chatRoomEntity.getCreatedDate())
                .isFull(isFull)
                .build();
    }
}
