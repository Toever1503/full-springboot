package com.repositories;

import com.entities.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface INotificationUserRepository extends JpaRepository<NotificationUser, Long> {

    //Make all notification read of user
    @Query("update NotificationUser u set u.isRead = true where u.userId=?1")
    @Modifying
   @Transactional
    void setReadAll(Long id);
    Optional<NotificationUser> findByUserIdAndNotificationId(Long currentUserId, long id);

    void deleteAllByNotificationId(Long notificationId);
}
