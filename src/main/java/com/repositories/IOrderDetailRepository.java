package com.repositories;

import com.entities.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface IOrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    @Query(value = "CALL order_completed(?1,?2, ?3)", nativeQuery = true)
    List<Integer> getQuantityProductByStatusAndTime(String status_order, Date time_from, Date time_to);
}
