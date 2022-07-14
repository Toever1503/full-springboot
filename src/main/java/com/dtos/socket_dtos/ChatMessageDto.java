package com.dtos.socket_dtos;

import com.entities.UserEntity;
import com.entities.chat.ChatMessageEntity;
import com.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private String message;
    private Long roomId;
    private List<String> attachments;
    private String sender;
    private List<String> senderRole;

    public static ChatMessageDto toChatMessageDto(ChatMessageEntity entity){

        ChatMessageDto dto = new ChatMessageDto();
        dto.setMessage(entity.getMessage());
        dto.setRoomId(entity.getChatRoom().getRoomId());
        if(entity.getAttachment() != null){
            dto.setAttachments(List.of(entity.getAttachment()));
        }
        dto.setSender(entity.getUser().getUserName());
        dto.setSenderRole(entity.getUser().getRoleEntity()
                .stream().map(RoleEntity::getRoleName).collect(Collectors.toList()));
        return dto;
    }
}
