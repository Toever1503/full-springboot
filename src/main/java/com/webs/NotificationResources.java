package com.webs;

import com.dtos.NotificationDetailDto;
import com.dtos.NotificationDto;
import com.dtos.ResponseDto;
import com.services.INotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationResources {

    private final INotificationService notificationService;

    public NotificationResources(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(this.notificationService.findAll(page).map(NotificationDto::toDto), "Admin get all notifications");
    }

    @GetMapping("user/getAll")
    public ResponseDto userGetAll(Pageable page) {
        return ResponseDto.of(this.notificationService.userGetAllNotifications(page), "User get all notifications");
    }

    @GetMapping("{id}")
    public ResponseDto getNotification(@PathVariable long id) {
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @GetMapping("increase-view/{id}")
    public ResponseDto viewNotification(@PathVariable long id) {
        return ResponseDto.of(this.notificationService.increaseView(id), "Increase view notification id: " + id);
    }
}
