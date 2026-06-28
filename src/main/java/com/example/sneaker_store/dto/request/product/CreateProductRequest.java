package com.example.sneaker_store.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm là bắt buộc")
    private String description;

    @NotNull(message = "Giá sản phẩm là bắt buộc")
    private Double price;
    
    @NotNull(message = "ID thương hiệu là bắt buộc")
    private Long brandId;

    @NotNull(message = "ID danh mục là bắt buộc")
    private Long categoryId;

    @NotBlank(message = "Tiêu đề mô tả là bắt buộc")
    private String title;
}
