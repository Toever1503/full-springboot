package com.services;

import com.entities.OrderEntity;
import com.models.OrderModel;

public interface IOrderService extends IBaseService<OrderEntity, OrderModel, Long> {
    OrderEntity updateStatusOrder(Long id, String status);
    OrderEntity cancelOrder(Long id);
}
