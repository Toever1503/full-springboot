package com.services.impl;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import com.entities.NotificationUser;
import com.models.NotificationModel;
import com.repositories.IUserRepository;
import com.repositories.NotificationRepository;
import com.services.INotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final IUserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, IUserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationEntity> findAll() {
        return null;
    }

    @Override
    public Page<NotificationEntity> findAll(Pageable page) {
        return this.notificationRepository.findAll(page);
    }

    @Override
    public Page<NotificationEntity> filter(Pageable page, Specification<NotificationEntity> specs) {
        return null;
    }

    @Override
    public NotificationEntity findById(Long id) {
        return null;
    }

    @Override
    public NotificationEntity add(NotificationModel model) {
        userRepository.getAll().stream().map(idView->  NotificationUser.builder()
                    .isRead(false)
                    .notificationId(null)
                    .userId(idView.getId())
                    .build()).collect(Collectors.toList());
        return null;
    }

    @Override
    public List<NotificationEntity> add(List<NotificationModel> model) {
        return null;
    }

    @Override
    public NotificationEntity update(NotificationModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public Page<NotificationDto> userGetAllNotifications(Pageable page) {
        Page<NotificationEntity> notificationEntityPage = this.findAll(page);
        return notificationEntityPage.map(notificationEntity -> {
            NotificationDto notificationDto = NotificationDto.toDto(notificationEntity);
            return null;
        });
    }
}
