package com.example.sneaker_store.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GetReviewPageResponse {
    private DataPage page;
    private List<Review> reviews;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPage {
        private int number;
        private int size;
        private int numberOfElements;
        private int totalPages;
    }

    @Getter
    @Setter
    public static class Review {
        private Long id;
        private String productName;
        private String email;
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
