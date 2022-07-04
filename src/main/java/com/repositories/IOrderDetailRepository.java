package com.repositories;

import com.dtos.OrderByStatusAndTimeDto;
import com.entities.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IOrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    @Procedure
    @Query(value = "CALL order_by_status_and_time(?1,?2, ?3)", nativeQuery = true)
    List<OrderByStatusAndTimeDto> order_by_status_and_time(@Param("status_order") String status_order, @Param("time_from") Date time_from, @Param("time_to") Date time_to);
}
