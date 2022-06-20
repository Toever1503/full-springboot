package com.services.impl;

import com.entities.ReviewEntity;
import com.entities.UserEntity;
import com.models.ReviewModel;
import com.repositories.IOrderDetailRepository;
import com.repositories.IProductRepository;
import com.repositories.IReviewRepository;
import com.services.IOrderService;
import com.services.IProductService;
import com.services.IReviewService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements IReviewService {
    final IReviewRepository reviewRepository;
    final FileUploadProvider fileUploadProvider;
    final IOrderService orderService;
    final IOrderDetailRepository orderDetailRepository;
    final IProductService productService;
    final IProductRepository productRepository;

    public ReviewServiceImpl(IReviewRepository reviewRepository, FileUploadProvider fileUploadProvider, IOrderService orderService, IOrderDetailRepository orderDetailRepository, IProductService productService, IProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.orderService = orderService;
        this.orderDetailRepository = orderDetailRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public List<ReviewEntity> findAll() {
        return null;
    }

    @Override
    public Page<ReviewEntity> findAll(Pageable page) {
        return this.reviewRepository.findAll(page);
    }

    @Override
    public Page<ReviewEntity> filter(Pageable page, Specification<ReviewEntity> specs) {
        return null;
    }

    @Override
    public ReviewEntity findById(Long id) {
        return this.reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public ReviewEntity add(ReviewModel model) {
        ReviewEntity reviewEntity = ReviewModel.toEntity(model);

        // KH da order thi moi cho review
        if(model.getOrderId() != null) {
            if(SecurityUtils.getCurrentUserId() == this.orderService.findById(model.getOrderId()).getCreatedBy().getId()) {
                this.orderService.findById(model.getOrderId()).getOrderDetails().forEach(orderDetail -> {
                    // cap nhat rating cho san pham
                    // neu chua danh gia thi duoc phep danh gia, con neu danh gia roi thi khong duocj danh gia nua ma chi duoc sua danh gia 1 lan
                    if (orderDetail.getOptionId() == model.getOptionId() && orderDetail.getProductId() == model.getProductId() && orderDetail.getIsReview() == false) {
                        reviewEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
                        orderDetail.setIsReview(true);
                        this.productService.findById(model.getProductId()).setRating((this.productService.findById(model.getProductId()).getRating() + model.getRating()) / 2);
                        this.orderDetailRepository.save(orderDetail);
                        this.productRepository.save(this.productService.findById(model.getProductId()));
                        reviewEntity.setProduct(this.productService.findById(model.getProductId()));
                    }else {
                        // khong duoc danh gia nua
                        new RuntimeException("You have reviewed this product");
                    }
                });
                reviewEntity.setOrder(this.orderService.findById(model.getOrderId()));
            }else {
                throw new RuntimeException("You can't review this order");
            }
        }

        if(model.getParentId() != null) {
            ReviewEntity parentReview = this.findById(model.getParentId());
            reviewEntity.setParentReview(parentReview);
        } else {
            reviewEntity.setParentReview(null);
        }

        String folder = UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + ReviewEntity.FOLDER;
        if (model.getAttachFiles() != null) {
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file : model.getAttachFiles()) {
                try {
                    filePaths.add(fileUploadProvider.uploadFile(folder, file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            reviewEntity.setAttachFiles(jsonObject.toString());
        }

        return this.reviewRepository.save(reviewEntity);
    }

    @Override
    public List<ReviewEntity> add(List<ReviewModel> model) {
        return null;
    }

    @Override
    public ReviewEntity update(ReviewModel model) {
        // neu KH da review thi chi cho phep sua 1 lan
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        this.reviewRepository.deleteById(id);
        this.reviewRepository.updateReviewParent(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
