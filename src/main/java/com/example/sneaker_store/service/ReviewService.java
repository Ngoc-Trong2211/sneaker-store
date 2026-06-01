package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.review.CreateReviewRequest;
import com.example.sneaker_store.dto.request.review.SpecificationReviewRequest;
import com.example.sneaker_store.dto.response.review.GetReviewPageResponse;
import com.example.sneaker_store.dto.response.review.GetReviewResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    void createReview(CreateReviewRequest req);
    boolean canReviewByOrderCodeAndProduct(String codeOrder, String productId);
    GetReviewResponse getReviewsByProduct(String productId);
    List<GetReviewResponse.Review> getReviewsByUserId(String userId);
    GetReviewPageResponse getReview(Pageable pageable, SpecificationReviewRequest req);
}
