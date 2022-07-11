package com.services;

import com.entities.ReviewEntity;
import com.models.ReviewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService extends IBaseService<ReviewEntity, ReviewModel, Long> {
    ReviewEntity responseReview(ReviewModel model);
    ReviewEntity updateStatus(Long id, String status);
    Page<ReviewEntity> findAllParentReviewIsNull( Pageable page);
    Page<ReviewEntity> findAllParentReviewIsNullAndStatusAndProductId(Pageable page, String status, Long productId);
    Page<ReviewEntity> findAllByParentId(Long id, Pageable pageable);
    Page<ReviewEntity> findAllMyReview(Long id, Pageable pageable);
}
