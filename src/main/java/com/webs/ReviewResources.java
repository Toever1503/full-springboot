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
        return ResponseDto.of(reviewService.findAll(page).map(ReviewDto::toDto), "Reviews retrieved successfully");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getReviewById(@PathVariable("id") Long id) {
        return ResponseDto.of(ReviewDto.toDto(this.reviewService.findById(id)), "Review retrieved successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addReview(@ModelAttribute ReviewModel reviewModel) {
        return ResponseDto.of(ReviewDto.toDto(reviewService.add(reviewModel)), "Review added successfully");
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseDto updateReview(@PathVariable Long id, @ModelAttribute ReviewModel reviewModel) {
        reviewModel.setId(id);
        return ResponseDto.of(ReviewDto.toDto(reviewService.update(reviewModel)), "Review updated successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteReview(@PathVariable Long id) {
        return ResponseDto.of(this.reviewService.deleteById(id), "Review deleted successfully");
    }

    //API update status
    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/{id}/updateStatus")
    public ResponseDto updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        ReviewEntity review = this.reviewService.updateStatus(id, status);
        this.eProductRepository.save(ProductDto.toDto(review.getProduct()));
        return ResponseDto.of(review, "Status updated successfully");
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
                "Reviews retrieved for product: ".concat(id.toString()));
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/reply")
    public ResponseDto replyReview(@ModelAttribute ReviewModel reviewAdinModel) {
        ReviewEntity reviewEntity = reviewService.responseReview(reviewAdinModel);
        this.eProductRepository.save(ProductDto.toDto(reviewEntity.getProduct()));
        return ResponseDto.of(ReviewDto.toDto(reviewEntity), "Review reply successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("/all-parent")
    public ResponseDto getAllParentReviews(Pageable page) {
        return ResponseDto.of(reviewService.findAllParentReviewIsNull(page).map(ReviewDto::toDto), "Reviews retrieved successfully");
    }

    @Transactional
    @GetMapping("/product/{productId}")
    public ResponseDto getAllParentReviewsByStatusAndProductId(Pageable page, @PathVariable("productId") Long productId) {
        String status = "APPROVED";
        return ResponseDto.of(reviewService.findAllParentReviewIsNullAndStatusAndProductId(page, status, productId).map(ReviewDto::toDto), "Reviews retrieved successfully");
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter( @RequestBody ReviewFilterModel model, Pageable page) throws ParseException {
        Page<ReviewEntity> reviewEntities = reviewService.filter(page, Specification.where(ReviewSpecification.filter(model)));
        return ResponseDto.of(reviewEntities.map(ReviewDto::toDto), "Get question by filter successfully");
    }

    @Transactional
    @GetMapping("/reply/{id}")
    public ResponseDto getReplyByParentId(@PathVariable("id") Long parentId, Pageable pageable) {
        return ResponseDto.of(this.reviewService.findAllByParentId(parentId, pageable).map(ReviewDto::toDto), "Get all review by parent review id: " + parentId);
    }

    @Transactional
    @GetMapping("/my-reviews/{orderId}")
    public ResponseDto getMyReviews(@PathVariable Long orderId) {
        return ResponseDto.of(this.reviewService.findAllMyReview(orderId).stream().map(ReviewDto::toDto)
                .collect(Collectors.toList()), "Get all review by user id: " + SecurityUtils.getCurrentUserId());
    }

}
