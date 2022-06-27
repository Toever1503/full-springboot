package com.services;

import com.entities.CartEntity;
import com.models.CartModel;

public interface ICartService extends IBaseService<CartEntity, CartModel, Long> {
    CartEntity editSku(CartModel model);
    CartEntity editQuantity(CartModel model);
}
