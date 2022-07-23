package com.repositories;

import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {

    //Update future notification posts native query
    @Query(value = "update tbl_notification set status = 'POSTED' where( notification_id>0 and status = 'FUTURE' and (future_date < now()))", nativeQuery = true)
    @Modifying
    @Transactional(rollbackFor = RuntimeException.class)
    void postCronNotifications();
    @Query(value = "select\n" +
            "        c.notification_id \n" +
            "    from\n" +
            "        tbl_notification c \n" +
            "    where\n" +
            "    c.status = 'FUTURE'\n" +
            "    and\n" +
            "\t\tc.future_date\n" +
            "        between \n" +
            "        date_sub(now(),interval 5 minute)\n" +
            "        and\n" +
            "        now()", nativeQuery = true)
    List<Long> findAllFutureIds();

    //Get all user's notification by userId and notification status
    @Query("select new com.dtos.NotificationDto(n.id, n.image, n.title, n.status, n.contentExcerpt, n.updatedDate , n.createdDate, n.isEdit, u.userName, n.viewed ,nu.isRead, n.attachFiles, n.category, n.url)" +
            " from NotificationEntity n join NotificationUser nu on n.id = nu.notificationId join UserEntity u on u.id = nu.userId where nu.userId= ?1 and n.status=?2")
    Page<NotificationDto> userGetAllNotifications(Long id, String status, Pageable page);
}
