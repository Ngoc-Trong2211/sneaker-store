package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.category.CreateCategoryRequest;
import com.example.sneaker_store.model.response.category.CreateCategoryResponse;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest req);
}
