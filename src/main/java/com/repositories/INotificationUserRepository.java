package com.repositories;

import com.entities.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface INotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    @Query("update NotificationUser u set u.isRead = true where u.userId.id=?1")
    @Modifying
    @Transactional
    void setReadAll(Long id);
}
