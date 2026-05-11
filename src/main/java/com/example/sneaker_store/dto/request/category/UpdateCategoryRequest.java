package com.example.sneaker_store.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryRequest {
    @NotNull(message = "id khong duoc de trong")
    private Long id;

    @NotBlank(message = "Name không được để trống")
    private String name;

    private String type;

    private Long parentId;
}
