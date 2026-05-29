package com.example.sneaker_store.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GetReviewResponse {
    private String productId;
    private Double averageRating;
    private Long totalReviews;
    private List<Review> reviews;

    @Getter
    @Setter
    public static class Review {
        private Long id;
        private String productId;
        private String userId;
        private String phone;
        private String orderCode;
        private Integer star;
        private Integer rating;
        private String comment;
        private String userName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
    }
}
