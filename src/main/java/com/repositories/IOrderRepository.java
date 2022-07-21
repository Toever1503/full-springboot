package com.repositories;

import com.dtos.OrderByStatusAndTimeDto;
import com.dtos.OrderGroupbyStatusDto;
import com.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    Page<OrderEntity> findAllByCreatedById(Long userId, Pageable pageable);

    Optional<OrderEntity> findByIdAndCreatedById(Long id, Long userId);

    Optional<OrderEntity> findByUuid(String uuid);

    @Modifying
    @Transactional
    @Query("update OrderEntity o set o.status=?1 where o.id = ?2")
    void changeOrderStatusByID(String status, Long id);

    @Modifying
    @Transactional
    @Query(value = "update tbl_order set status = 'CANCELED' where( order_id>0 and status = 'PAYING' and (UNIX_TIMESTAMP(updated_date) < UNIX_TIMESTAMP(DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL '7 15' HOUR_MINUTE))))",nativeQuery = true)
    void changeOrderStatusByIDAndTime();

    @Query("select c.status from OrderEntity c where c.id=?1 and c.createdBy.id = ?2")
    Optional<String> getStatusByID(Long id, Long uid);

    @Query("select c.redirectUrl from OrderEntity c where c.id=?1 and c.createdBy.id = ?2")
    Optional<String> getUrlByID(Long id, Long uid);

    @Query(value = "CALL all_by_status_and_time(?1,?2, ?3)", nativeQuery = true)
    List<Object[]> findAllByTimeAndStatus(String status_order, Date time_from, Date time_to);

    @Query(value = "CALL statistics_year_by_status_and_time(?1,?2, ?3)", nativeQuery = true)
    List<Object[]> statisticsYearByTimeAndStatus(String status_order, Date time_from, Date time_to);

    @Query(value = "CALL total_order_by_time_and_status(?1, ?2, ?3)", nativeQuery = true)
    List<Object[]> findTotalOrderByTimeAndStatus(String status_order, Date time_from, Date time_to);

    @Query(value = "CALL user_by_time(?1, ?2)", nativeQuery = true)
    List<Object[]> findTotalUserByTime(Date time_from, Date time_to);

    @Query(value = "select count(order_id) as quantity, status from tbl_order o group by status", nativeQuery = true)
    List<Object[]> findAllOrderGroupByStatus();
}
