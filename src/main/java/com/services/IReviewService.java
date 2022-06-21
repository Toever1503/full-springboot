package com.services;

import com.entities.ReviewEntity;
import com.models.ReviewModel;

public interface IReviewService extends IBaseService<ReviewEntity, ReviewModel, Long> {
    ReviewEntity responseReview(ReviewModel model);
    ReviewEntity updateStatus(Long id, String status);
}
