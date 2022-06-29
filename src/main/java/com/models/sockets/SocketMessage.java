package com.models.sockets;

import com.sun.mail.imap.protocol.UIDSet;
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
    private Long sendTo;
    private Set<Long> uidSet;
}
