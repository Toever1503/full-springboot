package com.dtos.socket_dtos;

import com.entities.chat.ChatMessageEntity;
import com.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String message;
    private Long roomId;
    private List<Object> attachments;
    private String sender;
    private String senderPrefix = "";
    private String senderRole;

    public static ChatMessageDto toChatMessageDto(ChatMessageEntity entity) {
        ChatMessageDto dto = new ChatMessageDto();
        String senderRole = RoleEntity.USER;
        if (entity.getUser().getRoleEntity().stream().anyMatch(x -> x.getRoleName().equals(RoleEntity.ADMINISTRATOR))) {
            senderRole = RoleEntity.ADMINISTRATOR;
            dto.senderPrefix = "Tư vấn viên ";
        }
        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setRoomId(entity.getChatRoom().getRoomId());
        if (entity.getAttachment() != null) {
            dto.setAttachments(new JSONObject(entity.getAttachment()).getJSONArray("files").toList());
        }else
            dto.setAttachments(List.of());
        dto.setSender(entity.getUser().getUserName());
        dto.setSenderRole(senderRole);
        return dto;
    }
}
