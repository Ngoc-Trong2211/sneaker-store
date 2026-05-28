package com.example.sneaker_store.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewGuestRequest {
    private String codeOrder;
    private String productId;

    @Min(1)
    @Max(5)
    private Integer star;
    private String comment;
}
