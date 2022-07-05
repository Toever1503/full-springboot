package com.models.socket_models;

import lombok.*;

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
    private Long sendTo;
    private Set<Long> uidSet;
}
