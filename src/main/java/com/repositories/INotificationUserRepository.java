package com.repositories;

import com.entities.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificationUserRepository extends JpaRepository<NotificationUser, Long> {
}
