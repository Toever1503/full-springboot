package com.repositories;

import com.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    
    Page<OrderEntity> findAllByCreatedById(Long userId, Pageable pageable);

    Optional<OrderEntity> findByIdAndCreatedById(Long id, Long userId);

    @Query("select c.status from OrderEntity c where c.id=?1 and c.createdBy.id = ?2")
    Optional<String> getStatusByID(Long id, Long uid);

    @Query("select c.redirectUrl from OrderEntity c where c.id=?1 and c.createdBy.id = ?2")
    Optional<String> getUrlByID(Long id, Long uid);
}
