package com.webs;

import com.dtos.*;
import com.entities.NotificationEntity;
import com.models.NotificationModel;
import com.models.filters.NotificationFilter;
import com.models.specifications.NotificationSpecification;
import com.services.INotificationService;
import com.services.ISocketService;
import com.utils.SecurityUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationResources {
    private final INotificationService notificationService;
    private final ISocketService socketService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public NotificationResources(INotificationService notificationService, ISocketService socketService) {
        this.notificationService = notificationService;
        this.socketService = socketService;
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping
    public ResponseDto addNotificationDetail(@Valid NotificationModel model) {
        log.info("admin {} is adding new notification", SecurityUtils.getCurrentUser().getUsername());
//        NotificationEntity notificationEntity = socketService.sendNotificationForAllUser(model,"abcdefgh.com");
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(this.notificationService.add(model));
        return ResponseDto.of(notificationDetailDto, "Added notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateNotification(@PathVariable Long id,@Valid NotificationModel model) {
        log.info("admin {} is updating notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        NotificationEntity notificationEntity = this.notificationService.update(model);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDetailDto, "update notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteNotification(@PathVariable("id") Long id) {
        log.info("admin {} is deleting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(this.notificationService.deleteById(id), "Deleted notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping
    public ResponseDto getAll(Pageable page) {
        log.info("admin {} is getting all notifications", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.notificationService.findAll(page).map(NotificationDto::toDto), "Admin get all notifications");
    }

    @Transactional
    @GetMapping("user/getAll")
    public ResponseDto userGetAll(Pageable page) {
        log.info("user {} is getting all notifications", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.notificationService.userGetAllNotifications(page), "User get all notifications");
    }


    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable Long id) {
        log.info("admin {} is getting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @Transactional
    @GetMapping("user/{id}")
    public ResponseDto userGetDetailQuestion(@PathVariable Long id) {
        log.info("user {} is getting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @ApiKeyAuthDefinition(name = "Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, key = "Authorization")
    @ApiOperation(value = "Increase view notification by id", notes = "Increase view notification by id")
    @Transactional
    @GetMapping("increase-view/{id}")
    public ResponseDto viewNotification(@PathVariable long id) {
        return ResponseDto.of(this.notificationService.increaseView(id), "Increase view notification id: " + id);
    }

    @Transactional
    @GetMapping("/mark-all-read")
    public ResponseDto setAllRead() {
        return ResponseDto.of(this.notificationService.setAllRead(), "All Read");
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody @Valid NotificationFilter notificationFilter, Pageable page) {
        return ResponseDto.of(this.notificationService.filter(page, Specification.where(NotificationSpecification.filter(notificationFilter))).map(NotificationDto::toDto), "Filter success");
    }

    @GetMapping("list-status")
    public ResponseDto getListNotificationStatus() {
        return ResponseDto.of(ENotificationStatus.values(), "Get list status successfully!");
    }
    @GetMapping("list-categories")
    public ResponseDto getListNotificationCategory() {
        return ResponseDto.of(ENotificationCategory.values(), "Get list categories successfully!");
    }
}
