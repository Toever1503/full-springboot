package com.services;

import com.dtos.*;
import com.entities.OrderEntity;
import com.models.OrderModel;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderService extends IBaseService<OrderEntity, OrderModel, Long> {
    OrderEntity updateStatusOrder(Long id, String status);

    OrderEntity cancelOrder(Long id);

    OrderEntity onlyUserFindById(Long id, Long userId);

    Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId);

    OrderEntity findByUUID(String uuid);

    String getStatusByID(Long id);

    String getUrlByID(Long id);

    OrderEntity updateDeliveryCode(Long id, String deliveryCode);

    List<OrderByStatusAndTimeDto> getAllOrderByStatusAndTime(String status_order, Date time_from, Date time_to);

    List<StatisticsYearByStatusAndTimeDto> statisticsYearOrderByStatusAndTime(String status_order, Date time_from, Date time_to);

    Map<Object, List<StatisticsYearByStatusAndTimeDto>> statisticsYearOrderByAndTime(Date time_from, Date time_to);

    Map<Object, List<StatisticsYearByStatusAndTimeDto>> statisticsYearOrderSelectStatus(List<String> status_orders ,Date time_from, Date time_to);

    List<TotalOrderWeekAndMonthByStatusAndTimeDto> getTotalOrderByStatusAndTime(String status_order, Date time_from, Date time_to);

    List<StatisticsUserDto> getTotalUserByTime(Date time_from, Date time_to);

    List<OrderGroupbyStatusDto> getAllOrderGroupByStatus();
}
