package com.services.impl;

import com.entities.ReviewEntity;
import com.models.ReviewModel;
import com.services.IReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements IReviewService {
    @Override
    public List<ReviewEntity> findAll() {
        return null;
    }

    @Override
    public Page<ReviewEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<ReviewEntity> filter(Pageable page, Specification<ReviewEntity> specs) {
        return null;
    }

    @Override
    public ReviewEntity findById(Long id) {
        return null;
    }

    @Override
    public ReviewEntity add(ReviewModel model) {
        return null;
    }

    @Override
    public List<ReviewEntity> add(List<ReviewModel> model) {
        return null;
    }

    @Override
    public ReviewEntity update(ReviewModel model) {
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
    public ReviewEntity responseReview(ReviewModel model) {
        return null;
    }

    @Override
    public ReviewEntity updateStatus(Long id, String status) {
        return null;
    }

    @Override
    public Page<ReviewEntity> findAllParentReviewIsNull(Pageable page) {
        return null;
    }

    @Override
    public Page<ReviewEntity> findAllParentReviewIsNullAndStatusAndProductId(Pageable page, String status, Long productId) {
        return null;
    }

    @Override
    public Page<ReviewEntity> findAllByParentId(Long id, Pageable pageable) {
        return null;
    }
}
