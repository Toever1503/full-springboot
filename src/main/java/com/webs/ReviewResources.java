package com.webs;

import com.dtos.ResponseDto;
import com.dtos.ReviewDto;
import com.entities.ReviewEntity;
import com.models.ReviewModel;
import com.services.IReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/reviews")
public class ReviewResources {
    final private IReviewService reviewService;

    public ReviewResources(IReviewService reviewService) {
        this.reviewService = reviewService;
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
        return ResponseDto.of(this.reviewService.updateStatus(id, status), "Status updated successfully");
    }


    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/reply")
    public ResponseDto replyReview(@ModelAttribute ReviewModel reviewAdinModel) {
        return ResponseDto.of(ReviewDto.toDto(reviewService.responseReview(reviewAdinModel)), "Review reply successfully");
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
}
