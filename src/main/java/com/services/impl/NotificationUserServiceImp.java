package com.services.impl;

import com.entities.NotificationUser;
import com.models.NotificationUserModel;
import com.repositories.INotificationUserRepository;
import com.services.INotificationUserService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotificationUserServiceImp implements INotificationUserService {
    final INotificationUserRepository notificationUserRepository;

    public NotificationUserServiceImp(INotificationUserRepository notificationUserRepository) {
        this.notificationUserRepository = notificationUserRepository;
    }

    @Override
    public List<NotificationUser> findAll() {
        return null;
    }

    @Override
    public Page<NotificationUser> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<NotificationUser> filter(Pageable page, Specification<NotificationUser> specs) {
        return null;
    }

    @Override
    public NotificationUser findById(Long id) {
        return null;
    }

    @Override
    public NotificationUser add(NotificationUserModel model) {
        return null;
    }

    @Override
    public List<NotificationUser> add(List<NotificationUserModel> model) {
        return null;
    }

    @Override
    public NotificationUser update(NotificationUserModel model) {
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
    public boolean setAllRead() {
        notificationUserRepository.setReadAll(SecurityUtils.getCurrentUserId());
        return true;
    }
}
