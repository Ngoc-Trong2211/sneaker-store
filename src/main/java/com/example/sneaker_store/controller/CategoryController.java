package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.category.CreateCategoryRequest;
import com.example.sneaker_store.dto.request.category.SpecificationCategoryRequest;
import com.example.sneaker_store.dto.request.category.UpdateCategoryRequest;
import com.example.sneaker_store.dto.response.category.CreateCategoryResponse;
import com.example.sneaker_store.dto.response.category.GetCategoryResponse;
import com.example.sneaker_store.dto.response.category.UpdateCategoryResponse;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/categories")
    @ApiMessage(message = "Update category thành công")
    @Operation(summary = "Update category", description = "Update category")
    public ResponseEntity<UpdateCategoryResponse> update(@RequestBody @Valid UpdateCategoryRequest req) {
        log.info("UPDATE CATEGORY");
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(this.categoryService.updateCategory(req));
    }

    @GetMapping("/categories")
    @ApiMessage(message = "Get category thành công")
    @Operation(summary = "Get category", description = "Get category")
    public ResponseEntity<GetCategoryResponse> get(@ParameterObject Pageable pageable,
                                                   SpecificationCategoryRequest name) {
        log.info("GET LIST CATEGORIES");
        return ResponseEntity.ok(this.categoryService.getCategory(pageable, name));
    }

    @GetMapping("/categories/all")
    @ApiMessage(message = "Get category thành công")
    @Operation(summary = "Get category", description = "Get category")
    public ResponseEntity<List<GetCategoryResponse.Category>> getAll() {
        log.info("GET LIST CATEGORIES");
        return ResponseEntity.ok(this.categoryService.getAll());
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage(message = "Delete category thành công")
    @Operation(summary = "Delete category", description = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE CATEGORY");
        this.categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
