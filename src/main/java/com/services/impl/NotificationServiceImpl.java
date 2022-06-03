package com.services.impl;

import com.entities.NotificationEntity;
import com.models.NotificationModel;
import com.repositories.NotificationRepository;
import com.services.INotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService {
    final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationEntity> findAll() {
        return null;
    }

    @Override
    public Page<NotificationEntity> findAll(Pageable page) {
        return null;
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

}
