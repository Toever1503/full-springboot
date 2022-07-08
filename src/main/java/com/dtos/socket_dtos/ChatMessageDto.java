package com.dtos.socket_dtos;

import com.entities.MessageEntity;
import com.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String message;
    private String roomId;
    private List<String> attachments;
    private String sender;
    private String senderRole;

    public static ChatMessageDto toChatMessageDto(MessageEntity entity){
        String senderRole = RoleEntity.USER;
        if(entity.getUser().getRoleEntity().stream().anyMatch(x->x.getRoleName().equals(RoleEntity.ADMINISTRATOR))){
            senderRole = RoleEntity.ADMINISTRATOR;
        }
        ChatMessageDto dto = new ChatMessageDto();
        dto.setMessage(entity.getMessage());
        dto.setRoomId(entity.getChatRoomEntity().getRoomId());
        if(entity.getAttachment() != null){
            dto.setAttachments(List.of(entity.getAttachment()));
        }
        dto.setSender(entity.getUser().getUserName());
        dto.setSenderRole(senderRole);
        return dto;
    }
}
