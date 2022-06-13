package com.webs;

import com.dtos.NotificationDetailDto;
import com.dtos.NotificationDto;
import com.dtos.ResponseDto;
import com.entities.NotificationEntity;
import com.models.NotificationModel;
import com.models.filters.NotificationFilter;
import com.models.specifications.NotificationSpectification;
import com.services.INotificationService;
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
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public NotificationResources(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    public ResponseDto addNotificationDetail(@Valid @RequestBody NotificationModel model) {
        log.info("admin {%s} is adding new notification", SecurityUtils.getCurrentUser().getUsername());
        NotificationEntity notificationEntity = this.notificationService.add(model);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);

        return ResponseDto.of(notificationDetailDto, "Added notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping("{id}")
    public ResponseDto updateNotification(@PathVariable Long id,@Valid @RequestBody NotificationModel model) {
        log.info("admin {%s} is updating notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        NotificationEntity notificationEntity = this.notificationService.update(model);
        NotificationDetailDto notificationDetailDto = NotificationDetailDto.toDto(notificationEntity);
        return ResponseDto.of(notificationDetailDto, "update notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/{id}")
    public ResponseDto deleteNotification(@PathVariable("id") Long id) {
        log.info("admin {%s} is deleting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(this.notificationService.deleteById(id), "Deleted notification successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping
    public ResponseDto getAll(Pageable page) {
        log.info("admin {%s} is getting all notifications", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.notificationService.findAll(page).map(NotificationDto::toDto), "Admin get all notifications");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("user/getAll")
    public ResponseDto userGetAll(Pageable page) {
        log.info("user {%s} is getting all notifications", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(this.notificationService.userGetAllNotifications(page), "User get all notifications");
    }


    @RolesAllowed("ADMINISTRATOR")
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable Long id) {
        log.info("admin {%s} is getting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("user/{id}")
    public ResponseDto userGetDetailQuestion(@PathVariable Long id) {
        log.info("user {%s} is getting notification id: {%d}", SecurityUtils.getCurrentUser().getUsername(), id);
        return ResponseDto.of(NotificationDetailDto.toDto(this.notificationService.findById(id)), "Get notification id: " + id);
    }

    @ApiKeyAuthDefinition(name = "Authorization", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, key = "Authorization")
    @ApiOperation(value = "Increase view notification by id", notes = "Increase view notification by id")
    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("increase-view/{id}")
    public ResponseDto viewNotification(@PathVariable long id) {
        return ResponseDto.of(this.notificationService.increaseView(id), "Increase view notification id: " + id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/mark-all-read")
    public ResponseDto setAllRead() {
        return ResponseDto.of(this.notificationService.setAllRead(), "All Read");
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody @Valid NotificationFilter notificationFilter, Pageable page) {
        return ResponseDto.of(this.notificationService.filter(page, Specification.where(NotificationSpectification.filter(notificationFilter))).map(NotificationDto::toDto), "Filter success");
    }
}
