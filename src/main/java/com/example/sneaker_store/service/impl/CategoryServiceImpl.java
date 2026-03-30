package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.model.request.category.CreateCategoryRequest;
import com.example.sneaker_store.model.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.model.response.category.CreateCategoryResponse;
import com.example.sneaker_store.model.response.category.UpdateCategoryResponse;
import com.example.sneaker_store.repository.CategoryRepository;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.util.exception.NameExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "CATEGORY-SERVICE")
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest req) {
        if (this.categoryRepository.existsByName(req.getName().toUpperCase()))
            throw new NameExistsException("Name is exists");
        CategoryEntity category = new CategoryEntity();
        category.setName(req.getName());
        category.setParentId(req.getParentId());
        this.categoryRepository.save(category);
        return this.modelMapper.map(category, CreateCategoryResponse.class);
    }

    @Override
    public UpdateCategoryResponse updateCategory(UpdateCategoryRequest req) {
        CategoryEntity category = this.categoryRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category!"));
        if (this.categoryRepository.existsByNameAndIdNot(req.getName(), req.getId())) {
            throw new RuntimeException("Tên category đã tồn tại!");
        }
        category.setName(req.getName());
        category.setParentId(req.getParentId());
        this.categoryRepository.save(category);
        return this.modelMapper.map(category, UpdateCategoryResponse.class);
    }
}
