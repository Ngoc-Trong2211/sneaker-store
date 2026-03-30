package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.category.CreateCategoryRequest;
import com.example.sneaker_store.model.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.model.response.category.CreateCategoryResponse;
import com.example.sneaker_store.model.response.category.UpdateCategoryResponse;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest req);
    UpdateCategoryResponse updateCategory(UpdateCategoryRequest req);
}
