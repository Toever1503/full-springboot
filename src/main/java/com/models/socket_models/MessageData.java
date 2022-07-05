package com.models.socket_models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
