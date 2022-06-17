package com.services;

import com.entities.OrderEntity;
import com.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService extends IBaseService<OrderEntity, OrderModel, Long> {
    OrderEntity updateStatusOrder(Long id, String status);
    OrderEntity cancelOrder(Long id);
    OrderEntity onlyUserFindById(Long id, Long userId);
    Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId);

    String getStatusByID(Long id);

    String getUrlByID(Long id);
}
