package com.models.socket_models;

import com.dtos.socket_dtos.ChatMessageDto;
import com.entities.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralSocketMessage {
    private String topic;
    private Object data;

    public static GeneralSocketMessage toGeneralSocketMessage(MessageEntity entity){
        GeneralSocketMessage socketMessage = new GeneralSocketMessage();
        socketMessage.setTopic("Chat");
        socketMessage.setData(ChatMessageDto.toChatMessageDto(entity));
        return socketMessage;
    }
}
