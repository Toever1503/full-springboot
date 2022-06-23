package com.services;

import com.entities.CartEntity;
import com.models.CartModel;
import com.models.ChangeOptionProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICartService extends IBaseService<CartEntity, CartModel, Long> {
    CartEntity updateQuantityProduct(Long id, Integer quantity);
    Page<CartEntity> findAllByUserId(Pageable pageable);
    CartEntity updateOptionProduct(Long id, ChangeOptionProductModel model);
}
