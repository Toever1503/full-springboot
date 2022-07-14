package com.models.chat_models;

import com.config.socket.SocketHandler;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.entities.chat.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChatMessageModel {
    private String message;
    private List<Object> attachment;
    private String sender;
    private String senderName;
    private String createdDate;

    public static ChatMessageModel toModel(ChatMessageEntity entity) {
        if (entity == null)
            throw new RuntimeException("ChatMessageEntity is null");

        boolean isAdministrator = UserEntity.hasRole(RoleEntity.ADMINISTRATOR, entity.getUser().getRoleEntity());
        String userName = UserEntity.getName(entity.getUser());

        return ChatMessageModel
                .builder()
                .message(entity.getMessage())
                .attachment(entity.getAttachment() == null ? List.of() : new JSONObject(entity.getAttachment()).getJSONArray("files").toList())
                .sender(isAdministrator ? RoleEntity.ADMINISTRATOR : RoleEntity.USER)
                .senderName(isAdministrator ? "Tư vấn viên ".concat(userName) : userName)
                .createdDate(entity.getCreatedDate().toString()).build();
    }
}
