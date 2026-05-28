package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.review.CreateReviewGuestRequest;
import com.example.sneaker_store.dto.request.review.CreateReviewRequest;

public interface ReviewService {
    void createReview(CreateReviewRequest req);
    void createReviewGuest(CreateReviewGuestRequest req);
}
