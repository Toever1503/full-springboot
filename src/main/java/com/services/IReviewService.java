package com.services;

import com.entities.ReviewEntity;
import com.models.ReviewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewService extends IBaseService<ReviewEntity, ReviewModel, Long> {
    ReviewEntity responseReview(ReviewModel model);
    ReviewEntity updateStatus(Long id, String status);
    Page<ReviewEntity> findAllParentReviewIsNull( Pageable page);
    Page<ReviewEntity> findAllParentReviewIsNullAndStatusAndProductId(Pageable page, String status, Long productId);
    Page<ReviewEntity> findAllByParentId(Long id, Pageable pageable);
    List<ReviewEntity> findAllMyReview(Long productId);
}
