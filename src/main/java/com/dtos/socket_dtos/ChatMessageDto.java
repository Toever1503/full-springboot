package com.dtos.socket_dtos;

import com.entities.chat.ChatMessageEntity;
import com.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String message;
    private Long roomId;
    private List<String> attachments;
    private String sender;
    private String senderRole;

    public static ChatMessageDto toChatMessageDto(ChatMessageEntity entity){
        String senderRole = RoleEntity.USER;
        if(entity.getUser().getRoleEntity().stream().anyMatch(x->x.getRoleName().equals(RoleEntity.ADMINISTRATOR))){
            senderRole = RoleEntity.ADMINISTRATOR;
        }
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setRoomId(entity.getChatRoom().getRoomId());
        if(entity.getAttachment() != null){
            dto.setAttachments(List.of(entity.getAttachment()));
        }
        dto.setSender(entity.getUser().getUserName());
        dto.setSenderRole(senderRole);
        return dto;
    }
}
