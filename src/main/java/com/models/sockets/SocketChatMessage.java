package com.models.sockets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketChatMessage {
    private String roomId;
    private MessageData messageData;
    private String topic;
}
