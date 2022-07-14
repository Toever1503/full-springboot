package com.models;

import com.entities.chat.ChatMessageEntity;
import com.entities.chat.ChatRoomEntity;
import com.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageModel {
    private String message;
    @NotNull
    private Long roomId;
    private List<MultipartFile> attachments;

    public static ChatMessageEntity toEntity(ChatMessageModel model, ChatRoomEntity roomEntity){
        return ChatMessageEntity
                .builder()
                .message(model.getMessage())
                .chatRoom(roomEntity)
                .user(SecurityUtils.getCurrentUser().getUser())
                .build();
    }
}
