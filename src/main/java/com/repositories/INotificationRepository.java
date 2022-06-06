package com.repositories;

import com.entities.NotificationEntity;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query(value = "update tbl_notification set status = 'POSTED' where( id>0 and status = 'FUTURE' and (UNIX_TIMESTAMP(future_date)) < UNIX_TIMESTAMP(DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL '0 7' DAY_HOUR)))",nativeQuery = true)
    @Modifying
    @Transactional
    void postCronNotifications();
}
