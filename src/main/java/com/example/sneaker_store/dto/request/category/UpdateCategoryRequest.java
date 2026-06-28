package com.example.sneaker_store.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryRequest {
    @NotNull(message = "ID danh mục không được để trống")
    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private String type;

    private Long parentId;
}
