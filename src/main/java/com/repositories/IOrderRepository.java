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
    @Query("SELECT o FROM OrderEntity o WHERE o.createdBy.id = ?1")
    Page<OrderEntity> findAllByCreatedBy_Id(Pageable pageable, Long userId);

    @Query("SELECT o FROM OrderEntity o WHERE o.id = ?1 and o.createdBy.id = ?2")
    Optional<OrderEntity> findByIdAndCreateBy_Id(Long id, Long userId);
}
