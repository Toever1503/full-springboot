package com.services.impl;

import com.dtos.ENotificationCategory;
import com.dtos.EStatusOrder;
import com.dtos.EStatusReview;
import com.entities.*;
import com.models.ReviewModel;
import com.models.SocketNotificationModel;
import com.repositories.*;
import com.services.*;
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
    final IOptionsRepository optionRepository;
    final ISocketService socketService; // REMOVE THIS LINE LATER
    private final INotificationService notificationService;
    private final IUserRepository userRepository;

    public ReviewServiceImpl(IReviewRepository reviewRepository, FileUploadProvider fileUploadProvider, IOrderService orderService, IOrderDetailRepository orderDetailRepository, IProductService productService, IProductRepository productRepository, IOptionsRepository optionRepository, ISocketService socketService, INotificationService notificationService, IUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.orderService = orderService;
        this.orderDetailRepository = orderDetailRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.socketService = socketService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
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
        return this.reviewRepository.findAll(specs, page);
    }

    @Override
    public ReviewEntity findById(Long id) {
        return this.reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public ReviewEntity add(ReviewModel model) {
        ReviewEntity reviewEntity = ReviewModel.toEntity(model);
        reviewEntity.setParentReview(null);
        OrderDetailEntity orderDetailEntity = this.orderDetailRepository.findById(model.getOrderDetailId()).orElseThrow(() -> new RuntimeException("Order detail not found"));

        // kiem tra nguoi dung da mua hang va da nhan hang chua
        // neu ok thi se set lai createBy, optionName,
        OrderEntity orderEntity = orderDetailEntity.getOrder();
        if (orderEntity == null) {
            throw new RuntimeException("Order not found");
        } else {
            if (orderEntity.getCreatedBy().getId().equals(SecurityUtils.getCurrentUserId()) && orderEntity.getStatus().equals(EStatusOrder.COMPLETED.name())) {
                reviewEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());

                orderDetailEntity.getProductId().getOptions().stream().forEach(option -> {
                    List<OptionEntity> optionEntities = this.optionRepository.findByOptionName(orderDetailEntity.getOptionId());
                    optionEntities.stream().forEach(optionEntity -> {
                        if (optionEntity.getId() == option.getId()) {
                            reviewEntity.setOptionName(optionEntity.getOptionName());
                        }
                    });
                });

                reviewEntity.setOrderDetail(orderDetailEntity);

                // set product, ten dat la productId nhung thuc te la product
                reviewEntity.setProduct(orderDetailEntity.getProductId());

                // upload file
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
            } else {
                throw new RuntimeException("You can not review this order, because you have not received it yet");
            }
        }
        ReviewEntity review = this.reviewRepository.save(reviewEntity);
        // set lai gia tri isReview cho orderDetail
        orderDetailEntity.setIsReview(true);
        this.orderDetailRepository.save(orderDetailEntity);
        // update gia tri rating cho product
        this.reviewRepository.updateProductRating(review.getProduct().getId());
        notificationService.addForSpecificUser(new SocketNotificationModel(null, SecurityUtils.getCurrentUsername() + " đã đánh giá cho sản phẩm!", "", ENotificationCategory.REVIEW, ReviewEntity.ADMIN_REVIEW_URL), this.userRepository.getAllIdsByRole(RoleEntity.ADMINISTRATOR));

        return review;
    }

    @Override
    public List<ReviewEntity> add(List<ReviewModel> model) {
        return null;
    }

    public JSONObject parseJson(String json) {
        return new JSONObject(json);
    }

    @Override
    public ReviewEntity update(ReviewModel model) {
        ReviewEntity originReview = this.findById(model.getId());
        final String folder = UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + ReviewEntity.FOLDER;

        ReviewEntity updateReview = ReviewModel.toEntity(model);
        updateReview.setParentReview(null);

        if (originReview.getStatus().equalsIgnoreCase(EStatusReview.PENDING.name()) && originReview.getCreatedBy().getId() == SecurityUtils.getCurrentUserId()) {
            updateReview.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
            updateReview.setIsEdit(true);
            OrderDetailEntity orderDetailEntity = this.orderDetailRepository.findById(model.getOrderDetailId()).orElseThrow(() -> new RuntimeException("Order detail not found"));
            // set optionId
            this.optionRepository.findByOptionName(orderDetailEntity.getOptionId()).stream().forEach(optionEntity -> {
                if (optionEntity.getOptionName().equalsIgnoreCase(originReview.getOptionName())) {
                    updateReview.setOptionName(optionEntity.getOptionName());
                }
            });
            // set product
            if (orderDetailEntity.getProductId().getId() != originReview.getProduct().getId()) {
                throw new RuntimeException("Product not found");
            } else {
                updateReview.setProduct(originReview.getProduct());
            }
            updateReview.setOrderDetail(orderDetailEntity);

            // upload file
            //delete file into s3
            List<Object> originalFile;
            if (originReview.getAttachFiles() != null) {
                originalFile = (parseJson(originReview.getAttachFiles()).getJSONArray("files").toList());
                originalFile.removeAll(model.getAttachFilesOrigin());
                originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
            }

            //add old file to uploadFiles
            List<String> uploadedFiles = new ArrayList<>();
            if (!model.getAttachFilesOrigin().isEmpty())
                uploadedFiles.addAll(model.getAttachFilesOrigin());

            //upload new file to uploadFiles and save to database
            if (model.getAttachFiles() != null) {
                for (MultipartFile file : model.getAttachFiles()) {
                    try {
                        uploadedFiles.add(fileUploadProvider.uploadFile(folder, file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            updateReview.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));
        } else {
            throw new RuntimeException("You can not update this review, because you have not created it yet or replyed it");
        }
        ReviewEntity reviewUpdate = this.reviewRepository.save(updateReview);
        this.reviewRepository.updateProductRating(reviewUpdate.getProduct().getId());
        notificationService.addForSpecificUser(new SocketNotificationModel(null, SecurityUtils.getCurrentUsername() + " đã chỉnh sửa lại đánh giá cho sản phẩm!", "", ENotificationCategory.REVIEW, ReviewEntity.ADMIN_REVIEW_URL), this.userRepository.getAllIdsByRole(RoleEntity.ADMINISTRATOR));
        return reviewUpdate;
    }

    @Override
    public boolean deleteById(Long id) {
        ReviewEntity review = this.findById(id);
        this.reviewRepository.deleteById(id);
        this.reviewRepository.deleteReviewByParent(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public ReviewEntity responseReview(ReviewModel model) {
        ReviewEntity reviewEntity = ReviewModel.toEntity(model);
        reviewEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        reviewEntity.setRating(null);
        reviewEntity.setStatus(EStatusReview.APPROVED.name());
        if (model.getParentId() != null) {
            if (model.getOrderDetailId() != this.findById(model.getParentId()).getOrderDetail().getId()) {
                throw new RuntimeException("Order detail not found");
            }
            ReviewEntity parentReview = this.findById(model.getParentId());
            parentReview.setStatus(EStatusReview.APPROVED.name());
            this.reviewRepository.save(parentReview);
            reviewEntity.setParentReview(parentReview);
            reviewEntity.setProduct(parentReview.getProduct());
            reviewEntity.setOptionName(parentReview.getOptionName());
            reviewEntity.setOrderDetail(parentReview.getOrderDetail());
            // file upload
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
        } else {
            throw new RuntimeException("Parent review not found");
        }
        notificationService.addForSpecificUser(new SocketNotificationModel(null, "Admin đã phản hồi lại đánh giá của bạn!", "", ENotificationCategory.REVIEW, ReviewEntity.ADMIN_REVIEW_URL), List.of(reviewEntity.getCreatedBy().getId()));
        return this.reviewRepository.save(reviewEntity);
    }

    public ReviewEntity updateStatus(Long id, String status) {
        ReviewEntity reviewEntity = this.findById(id);
        reviewEntity.setStatus(status);
        return this.reviewRepository.save(reviewEntity);
    }

    @Override
    public Page<ReviewEntity> findAllParentReviewIsNull(Pageable page) {
        return this.reviewRepository.findAllByParentReviewIsNull(page);
    }

    @Override
    public Page<ReviewEntity> findAllParentReviewIsNullAndStatusAndProductId(Pageable page, String status, Long productId) {
        return this.reviewRepository.findAllByParentReviewIsNullAndStatusAndProductId(page, status, productId);
    }

    @Override
    public Page<ReviewEntity> findAllByParentId(Long id, Pageable pageable) {
        return this.reviewRepository.findReviewEntityByParentReview(id, pageable);
    }

}
