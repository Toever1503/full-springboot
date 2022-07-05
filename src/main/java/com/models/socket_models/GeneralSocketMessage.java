package com.models.socket_models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralSocketMessage {
    private String topic;
    private Object data;
}
