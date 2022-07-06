package com.services;

import com.dtos.OrderByStatusAndTimeDto;
import com.dtos.OrderGroupbyStatusDto;
import com.entities.OrderEntity;
import com.models.OrderModel;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

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

    Integer getTotalOrderByStatusAndTime(String status_order, Date time_from, Date time_to);

    Double getTotalPriceByStatusAndTime(String status_order, Date time_from, Date time_to);

    Integer getTotalUserByTime(Date time_from, Date time_to);

    List<OrderGroupbyStatusDto> getAllOrderGroupByStatus();
}
