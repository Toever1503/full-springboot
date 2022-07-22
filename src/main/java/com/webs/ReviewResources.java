package com.webs;

import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.dtos.ReviewDto;
import com.entities.ReviewEntity;
import com.entities.ReviewEntity_;
import com.entities.RoleEntity;
import com.models.ReviewModel;
import com.models.filters.ReviewFilterModel;
import com.models.specifications.ReviewSpecification;
import com.services.IProductService;
import com.services.IReviewService;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.text.ParseException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewResources {
    final private IReviewService reviewService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final IEProductRepository eProductRepository;

    public ReviewResources(IReviewService reviewService, IEProductRepository eProductRepository) {
        this.reviewService = reviewService;
        this.eProductRepository = eProductRepository;
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping
    public ResponseDto getAllReviews(Pageable page) {
        return ResponseDto.of(reviewService.findAll(page).map(ReviewDto::toDto), "Lấy toàn bộ đánh giá");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getReviewById(@PathVariable("id") Long id) {
        return ResponseDto.of(ReviewDto.toDto(this.reviewService.findById(id)), "Lấy đánh giá theo id: " + id);
    }

    @Transactional
    @PostMapping
    public ResponseDto addReview(@ModelAttribute ReviewModel reviewModel) {
        return ResponseDto.of(ReviewDto.toDto(reviewService.add(reviewModel)), "Thêm đánh giá");
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseDto updateReview(@PathVariable Long id, @ModelAttribute ReviewModel reviewModel) {
        reviewModel.setId(id);
        return ResponseDto.of(ReviewDto.toDto(reviewService.update(reviewModel)), "Cập nhật đánh giá");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteReview(@PathVariable Long id) {
        return ResponseDto.of(this.reviewService.deleteById(id), "Xóa đánh giá có id: "+id);
    }

    //API update status
    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/{id}/updateStatus")
    public ResponseDto updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        ReviewEntity review = this.reviewService.updateStatus(id, status);
        this.eProductRepository.save(ProductDto.toDto(review.getProduct()));
        return ResponseDto.of(review, "Cập nhật trạng thái đánh giá");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("all-reviews-by-product/{id}")
    public ResponseDto getAllReviewsForProduct(@PathVariable Long id, Pageable page, @RequestParam("rating") Float rating) {
        Specification<ReviewEntity> spec = ReviewSpecification.byProductId(id);
        Specification<ReviewEntity> ratingSpec = ReviewSpecification.byRating(rating);
        Specification<ReviewEntity> parentSpec = ((root, query, criteriaBuilder) -> root.get(ReviewEntity_.PARENT_REVIEW).isNull());

        Specification<ReviewEntity> finalSpec;
        if (rating != 0)
            finalSpec = Specification.where(spec).and(ratingSpec).and(parentSpec);
        else
            finalSpec = Specification.where(spec).and(parentSpec);

        Page<ReviewEntity> reviewEntityPage = this.reviewService.filter(page, finalSpec);
        return ResponseDto.of(reviewEntityPage.map(ReviewDto::toDto),
                "Lấy đánh giá cho sản phẩm có id: ".concat(id.toString()));
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/reply")
    public ResponseDto replyReview(@ModelAttribute ReviewModel reviewAdinModel) {
        ReviewEntity reviewEntity = reviewService.responseReview(reviewAdinModel);
        this.eProductRepository.save(ProductDto.toDto(reviewEntity.getProduct()));
        return ResponseDto.of(ReviewDto.toDto(reviewEntity), "Phản hồi đánh giá");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("/all-parent")
    public ResponseDto getAllParentReviews(Pageable page) {
        return ResponseDto.of(reviewService.findAllParentReviewIsNull(page).map(ReviewDto::toDto), "Lấy toàn bộ đánh giá cha");
    }

    @Transactional
    @GetMapping("/product/{productId}")
    public ResponseDto getAllParentReviewsByStatusAndProductId(Pageable page, @PathVariable("productId") Long productId) {
        String status = "APPROVED";
        return ResponseDto.of(reviewService.findAllParentReviewIsNullAndStatusAndProductId(page, status, productId).map(ReviewDto::toDto), "Lấy toàn bộ đánh giá cha theo trạng thái và sản phẩm có id: "+productId);
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter( @RequestBody ReviewFilterModel model, Pageable page) throws ParseException {
        Page<ReviewEntity> reviewEntities = reviewService.filter(page, Specification.where(ReviewSpecification.filter(model)));
        return ResponseDto.of(reviewEntities.map(ReviewDto::toDto), "Lọc đánh giá");
    }

    @Transactional
    @GetMapping("/reply/{id}")
    public ResponseDto getReplyByParentId(@PathVariable("id") Long parentId, Pageable pageable) {
        return ResponseDto.of(this.reviewService.findAllByParentId(parentId, pageable).map(ReviewDto::toDto), "Lấy toàn bộ đánh giá theo đánh giá cha có id: " + parentId);
    }

    @Transactional
    @GetMapping("/my-reviews/{orderId}")
    public ResponseDto getMyReviews(@PathVariable Long orderId) {
        return ResponseDto.of(this.reviewService.findAllMyReview(orderId).stream().map(ReviewDto::toDto)
                .collect(Collectors.toList()), "Lấy toàn bộ đánh giá theo người dùng có id: " + SecurityUtils.getCurrentUserId());
    }

}
