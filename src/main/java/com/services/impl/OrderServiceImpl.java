package com.services.impl;

import com.entities.OrderEntity;
import com.models.OrderModel;
import com.services.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {
    @Override
    public List<OrderEntity> findAll() {
        return null;
    }

    @Override
    public Page<OrderEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<OrderEntity> filter(Pageable page, Specification<OrderEntity> specs) {
        return null;
    }

    @Override
    public OrderEntity findById(Long id) {
        return null;
    }

    @Override
    public OrderEntity add(OrderModel model) {
        return null;
    }

    @Override
    public List<OrderEntity> add(List<OrderModel> model) {
        return null;
    }

    @Override
    public OrderEntity update(OrderModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public OrderEntity updateStatusOrder(Long id, String status) {
        return null;
    }

    @Override
    public OrderEntity cancelOrder(Long id) {
        return null;
    }

    @Override
    public OrderEntity onlyUserFindById(Long id, Long userId) {
        return null;
    }

    @Override
    public Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId) {
        return null;
    }

    @Override
    public OrderEntity findByUUID(String uuid) {
        return null;
    }

    @Override
    public String getStatusByID(Long id) {
        return null;
    }

    @Override
    public String getUrlByID(Long id) {
        return null;
    }

    @Override
    public OrderEntity updateDeliveryCode(Long id, String deliveryCode) {
        return null;
    }
}
