package com.repositories;

import com.entities.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

public interface IOrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

//    @Query(value = "CALL order_by_status_and_time(?1,?2, ?3)", nativeQuery = true)
//    List<Object> order_by_status_and_time(@Param("status_order") String status_order, @Param("time_from") Date time_from, @Param("time_to") Date time_to);

    @Query(value = "CALL total_order_by_time_and_status(?1, ?2, ?3)", nativeQuery = true)
    Integer findTotalOrderByTimeAndStatus(String status_order, Date time_from, Date time_to);

}
