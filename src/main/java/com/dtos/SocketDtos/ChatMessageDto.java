package com.dtos.SocketDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String message;
    private String roomId;
    private List<String> attachments;
    private String sender;
    private String senderRole;
}
