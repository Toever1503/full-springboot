package com.repositories;

import com.entities.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long>, JpaSpecificationExecutor<NotificationUser> {

    void deleteAllByNotificationId(Long id);
}
