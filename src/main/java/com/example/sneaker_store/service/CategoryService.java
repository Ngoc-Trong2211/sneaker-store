package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.category.SpecificationCategoryRequest;
import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.dto.request.category.CreateCategoryRequest;
import com.example.sneaker_store.dto.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.dto.response.category.CreateCategoryResponse;
import com.example.sneaker_store.dto.response.category.GetCategoryResponse;
import com.example.sneaker_store.dto.response.category.UpdateCategoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest req);
    UpdateCategoryResponse updateCategory(UpdateCategoryRequest req);
    GetCategoryResponse getCategory(Pageable pageable, SpecificationCategoryRequest name);
    void deleteCategory(Long id);
    CategoryEntity findById(Long id);
    List<GetCategoryResponse.Category> getAll();
    CategoryEntity findBySlug(String slug);
}
