package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.category.CreateCategoryRequest;
import com.example.sneaker_store.model.response.category.CreateCategoryResponse;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-CONTROLLER")
@RequestMapping("/category/v1")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ApiMessage(message = "Tạo category thành công")
    @Operation(summary = "Create category", description = "Tạo mới category")
    public ResponseEntity<CreateCategoryResponse> create(@RequestBody @Valid CreateCategoryRequest req) {
        log.info("CREATE CATEGORY");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.categoryService.createCategory(req));
    }
}
