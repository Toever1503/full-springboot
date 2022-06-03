package com.repositories;

import com.entities.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long>, JpaSpecificationExecutor<NotificationUser> {

    Optional<NotificationUser> findByUserIdAndNotificationId(Long userId, Long notificationId);
    void deleteAllByNotificationId(Long id);
}
