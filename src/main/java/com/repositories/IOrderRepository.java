package com.repositories;

import com.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    
    Page<OrderEntity> findAllByCreatedById(Long userId, Pageable pageable);

    Optional<OrderEntity> findByIdAndCreatedById(Long id, Long userId);
    Optional<OrderEntity> findByUuid(String uuid);

    @Modifying
    @Transactional
    @Query("update OrderEntity o set o.status=?1 where o.id = ?2")
    void changeOrderStatusByID(String status,Long id);

    @Modifying
    @Transactional
    @Query(value = "update tbl_order set status = 'CANCELED' where( id>0 and status = 'PAYING' and (UNIX_TIMESTAMP(updated_date) < UNIX_TIMESTAMP(DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL '7 15' HOUR_MINUTE))))",nativeQuery = true)
    void changeOrderStatusByIDAndTime();
}
