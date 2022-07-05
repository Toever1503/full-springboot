package com.models.socket_models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketChatMessage {
    private String roomId;
    private MessageData messageData;
    private String topic;
}
