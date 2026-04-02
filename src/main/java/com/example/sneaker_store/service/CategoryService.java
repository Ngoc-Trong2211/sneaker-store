package com.example.sneaker_store.service;

import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.model.request.category.CreateCategoryRequest;
import com.example.sneaker_store.model.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.model.response.category.CreateCategoryResponse;
import com.example.sneaker_store.model.response.category.GetCategoryResponse;
import com.example.sneaker_store.model.response.category.UpdateCategoryResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest req);
    UpdateCategoryResponse updateCategory(UpdateCategoryRequest req);
    GetCategoryResponse getCategory(Pageable pageable, String name);
    void deleteCategory(Long id);
    CategoryEntity findById(Long id);
}
