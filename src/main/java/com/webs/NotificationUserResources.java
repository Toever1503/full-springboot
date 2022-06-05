package com.webs;

import com.dtos.ResponseDto;
import com.services.INotificationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification-user")
public class NotificationUserResources {
    @Autowired
    INotificationUserService notificationUserService;
    @GetMapping("/allread")
    public ResponseDto setAllRead(){
        return ResponseDto.of(notificationUserService.setAllRead(),"All Read");
    }
    @GetMapping("/oneread/{id}")
    public ResponseDto setOneRead(@PathVariable("id") Long id){
        return ResponseDto.of(notificationUserService.setOneRead(id),"One Read");
    }
}
