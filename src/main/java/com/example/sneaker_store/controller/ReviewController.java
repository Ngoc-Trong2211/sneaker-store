package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.dto.request.review.SpecificationReviewRequest;
import com.example.sneaker_store.dto.response.review.GetReviewPageResponse;
import com.example.sneaker_store.dto.response.review.GetReviewResponse;
import com.example.sneaker_store.service.ReviewService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "REVIEW-CONTROLLER")
@RequestMapping("/review/v1")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @Operation(summary = "Create review", description = "Create a review for a purchased product")
    @ApiMessage(message = "Review created successfully")
    public ResponseEntity<Void> createReview(@RequestBody @Valid CreateReviewRequest request) {
        reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reviews/check-eligibility")
    @Operation(summary = "Check review eligibility", description = "Check order code and product can be reviewed")
    @ApiMessage(message = "Check review eligibility successfully")
    public ResponseEntity<Boolean> checkReviewEligibility(
            @RequestParam String codeOrder,
            @RequestParam String productId
    ) {
        return ResponseEntity.ok(reviewService.canReviewByOrderCodeAndProduct(codeOrder, productId));
    }

    @GetMapping("/reviews/product/{productId}")
    @Operation(summary = "Get reviews by product", description = "Get rating summary and reviews of a product")
    @ApiMessage(message = "Get reviews by product successfully")
    public ResponseEntity<GetReviewResponse> getReviewsByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/reviews/user/{userId}")
    @Operation(summary = "Get reviews by user", description = "Get all reviews of a user")
    @ApiMessage(message = "Get reviews by user successfully")
    public ResponseEntity<List<GetReviewResponse.Review>> getReviewsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @GetMapping("/reviews")
    @Operation(summary = "Get reviews", description = "Get reviews with pagination and filter")
    @ApiMessage(message = "Get reviews successfully")
    public ResponseEntity<GetReviewPageResponse> getReviews(@ParameterObject Pageable pageable, SpecificationReviewRequest request) {
        return ResponseEntity.ok(reviewService.getReview(pageable, request));
    }
}
