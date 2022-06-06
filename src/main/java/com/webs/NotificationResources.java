package com.webs;

import com.dtos.NotificationDetailDto;
import com.dtos.NotificationDto;
import com.dtos.ResponseDto;
import com.services.INotificationService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping
    public ResponseDto addNotificationDetail(NotificationModel model) {
        NotificationEntity notificationEntity = this.notificationService.add(model);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDetailDto, "Added notification successfully");
    }

    @PutMapping
    public ResponseDto updateNotification(NotificationModel model) {
        NotificationEntity notificationEntity = this.notificationService.update(model);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDetailDto, "update notification successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseDto deleteNotification(@PathVariable("id") Long id) {
        return ResponseDto.of(this.notificationService.deleteById(id), "Deleted notification successfully");
    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(this.notificationService.findAll(page).map(NotificationDto::toDto), "Admin get all notifications");
    }

    @Transactional
    @GetMapping("user/getAll")
    public ResponseDto userGetAll(Pageable page) {
        return ResponseDto.of(this.notificationService.userGetAllNotifications(page), "User get all notifications");
    }


    @Transactional
    @GetMapping("{id}")
    public ResponseDto getNotification(@PathVariable long id) {
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @ApiKeyAuthDefinition(name = "Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, key = "Authorization")
    @ApiOperation(value = "Increase view notification by id", notes = "Increase view notification by id")
    @Transactional
    @GetMapping("increase-view/{id}")
    public ResponseDto viewNotification(@PathVariable long id) {
        return ResponseDto.of(this.notificationService.increaseView(id), "Increase view notification id: " + id);
    }
}
