package com.models.sockets;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SocketMessage {
    private String action;
    private MessageData messageData;
    private String topic;
    private SendTo sendTo;
    private Set<Long> uidSet;
}
