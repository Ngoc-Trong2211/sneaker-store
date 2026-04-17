package com.example.sneaker_store.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {
    @NotBlank(message = "Name không được để trống")
    private String name;

    private Long parentId;
}
