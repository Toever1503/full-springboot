//package com.services.impl;
//
//import com.config.socket.SocketHandler;
//import com.dtos.SocketDtos.ChatRoomDto;
//import com.entities.UserEntity;
//import com.models.sockets.SocketChatMessage;
//import com.services.IChatService;
//import com.services.IUserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.WebSocketSession;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ChatServiceImp implements IChatService {
//    @Autowired
//    SocketHandler socketHandler;
//    @Autowired
//    IUserService userService;
//
//    @Override
//    public SocketChatMessage sendMessage(SocketChatMessage socketChatMessage) {
//        return null;
//    }
//
//    @Override
//    public boolean createChatRoom(SocketChatMessage socketChatMessage) {
//        return false;
//    }
////        request.setAttribute("");
//
//    @Override
//    public boolean joinChatRoom(SocketChatMessage socketChatMessage) {
//        return false;
//    }
//
//    @Override
//    public List<ChatRoomDto> getAllRoomList() {
//        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
//        for (Map.Entry<String, Long> entry : socketHandler.getAllChatRoomAndUser().entrySet()) {
//            UserEntity user = userService.findById(entry.getValue());
//            ChatRoomDto chatRoomDto = new ChatRoomDto(entry.getKey(), user.getUserName(), Calendar.getInstance().getTime(), user.getAvatar());
//            chatRoomDtos.add(chatRoomDto);
//        }
//        return chatRoomDtos;
//    }
//
//    @Override
////    public List<ChatRoomDto> getAvailableRoomList() {
////        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
////        for (Map.Entry<String, Long> entry : socketHandler.getAllAvailableChatRoom().entrySet()) {
////            UserEntity user = userService.findById(entry.getValue());
////            ChatRoomDto chatRoomDto = new ChatRoomDto(entry.getKey(), user.getUserName(), Calendar.getInstance().getTime(), user.getAvatar());
////            chatRoomDtos.add(chatRoomDto);
////        }
////        return chatRoomDtos;
////    }
//
//    @Override
//    public List<ChatRoomDto> getFullRoomList() {
//        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
//        for (Map.Entry<String, Long> entry : socketHandler.getAllFullChatRoom().entrySet()) {
//            UserEntity user = userService.findById(entry.getValue());
//            ChatRoomDto chatRoomDto = new ChatRoomDto(entry.getKey(), user.getUserName(), Calendar.getInstance().getTime(), user.getAvatar());
//            chatRoomDtos.add(chatRoomDto);
//        }
//        return chatRoomDtos;
//    }
//}
//
