package com.webs;

import com.config.socket.SocketHandler;
import com.dtos.ResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.models.NotificationModel;
import com.models.sockets.SendTo;
import com.models.sockets.SocketMessage;
import com.services.INotificationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.Set;


@RestController
@RequestMapping("test")
public class TestRestController {

    @Autowired
    SocketHandler socketHandler;

    @Autowired
    INotificationService notificationService;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FormData {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date date;
    }

    @PostMapping
    public FormData getFormData(@RequestBody FormData form) {
        return form;
    }


    @Transactional
    @PostMapping("publish-notification")
    public void pub(@Valid NotificationModel model) {
        SocketMessage socketMessage = new SocketMessage("publish", null, "notification", SendTo.USER, Set.of(1l));
        socketHandler.publishNotification(socketMessage, this.notificationService.addForSpecificUser(model, 1l, "localhost:210949124"));
    }

    @Transactional
    @PostMapping("publish-notification-all")
    public void pub(@Valid NotificationModel model, @RequestParam("uid") Long uid) {
        SocketMessage socketMessage = new SocketMessage("publish", null, "notification", SendTo.ALL, Set.of(1l, 19l, 25l));
        socketHandler.publishNotification(socketMessage, this.notificationService.add(model));
    }

}
