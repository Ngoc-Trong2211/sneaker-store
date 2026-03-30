package com.example.sneaker_store.model.response.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryResponse {
    private String name;
    private String slug;
    private Long parentId;
}
