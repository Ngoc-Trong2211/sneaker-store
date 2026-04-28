package com.example.sneaker_store.dto.response.product;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProductByIdResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private String status;
    private Long brandId;
    private Long categoryId;
    private List<String> images;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
}
