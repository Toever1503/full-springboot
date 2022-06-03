package com.services.impl;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import com.entities.NotificationUser;
import com.models.NotificationModel;
import com.repositories.IUserRepository;
import com.repositories.NotificationRepository;
import com.repositories.NotificationUserRepository;
import com.services.INotificationService;
import com.utils.SecurityUtils;
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
    private final NotificationUserRepository notificationUserRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, IUserRepository userRepository, NotificationUserRepository notificationUserRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationUserRepository = notificationUserRepository;
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
        return this.notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found notification id: " + id));
    }

    @Override
    public NotificationEntity add(NotificationModel model) {
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
        return this.notificationRepository.userGetAllNotifications(SecurityUtils.getCurrentUserId(), "POSTED", page);
    }

    @Override
    public boolean increaseView(long id) {
        NotificationEntity entity = this.findById(id);
        entity.setViewed(entity.getViewed() == null ? 0 : entity.getViewed() + 1);
        this.notificationRepository.save(entity);
        return true;
    }
}
