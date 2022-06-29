package com.models.sockets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketMessage;

import java.sql.Timestamp;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageData{
    private Long id;
    private String messageContent;
    private Boolean messageStatus;
    private Date createdDate;
}
