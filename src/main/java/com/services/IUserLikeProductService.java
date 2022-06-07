package com.services;

import com.entities.UserLikeProductEntity;
import com.models.UserLikeProductModel;

public interface IUserLikeProductService extends IBaseService<UserLikeProductEntity, UserLikeProductModel, Long>{

    int likeProduct(Long id);
}
