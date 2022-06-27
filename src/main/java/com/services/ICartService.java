package com.services;

import com.entities.CartEntity;
import com.models.CartModel;
import com.models.ChangeOptionModel;

public interface ICartService extends IBaseService<CartEntity, CartModel, Long> {
    CartEntity changeOption(ChangeOptionModel model);
    CartEntity editQuantity(CartModel model);
}
