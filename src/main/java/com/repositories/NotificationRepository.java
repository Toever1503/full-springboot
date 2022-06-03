package com.repositories;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
//    Page<NotificationDto> userGetAllNotifications(Pageable page);

}
