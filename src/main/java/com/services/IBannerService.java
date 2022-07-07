package com.services;

import com.entities.BannerEntity;
import com.models.BannerModel;

public interface IBannerService extends IBaseService<BannerEntity, BannerModel, Long> {
    BannerEntity updateStatus(Long id, String status);
}
