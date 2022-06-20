package com.webs;

import com.dtos.ResponseDto;
import com.models.ReviewModel;
import com.services.IReviewService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewResources {
    final private IReviewService reviewService;

    public ReviewResources(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAllReviews() {
        return ResponseDto.of(reviewService.findAll(), "Reviews retrieved successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addReview(@ModelAttribute ReviewModel reviewModel) {
        return ResponseDto.of(reviewService.add(reviewModel), "Review added successfully");
    }
}
