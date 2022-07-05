package com.models.socket_models;

import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketChatUserModel {
    private Long userId;
    private List<ChatRoomModel> chatRoomList;
}
