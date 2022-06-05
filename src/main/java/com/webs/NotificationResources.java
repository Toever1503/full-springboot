package com.webs;

import com.dtos.NotificationDetailDto;
import com.dtos.NotificationDto;
import com.dtos.ResponseDto;
import com.entities.NotificationEntity;
import com.models.NotificationModel;
import com.services.INotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/notification")
public class NotificationResources {
    private final INotificationService notificationService;

    public NotificationResources(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseDto getNotification(Pageable pageable) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseDto getNotification(@PathVariable Long id) {
        NotificationEntity notificationEntity = notificationService.findById(id);
        NotificationDto notificationDto = NotificationDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDto, "Get notification success");
    }

    @GetMapping("detail/{id}")
    public ResponseDto getNotificationDetail(@PathVariable Long id) {
        NotificationEntity notificationEntity = notificationService.findById(id);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDetailDto, "Get notification detail success");
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
    }
}
