package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.service.ReviewService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
