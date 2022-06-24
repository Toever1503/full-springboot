package com.config.socket.exception;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

public class ChatRoomException extends RuntimeException {
    private String topic;
    private String roomId;
    private String message;

    public ChatRoomException(String topic, String roomId, String message) {
        super(message);
        this.message = message;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }
}
