package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "BRAND-CONTROLLER")
@RequestMapping("/brand/v1")
public class BrandController {
    private final BrandService brandService;

    @PostMapping("/brands")
    @ApiMessage(message = "Tạo brand thành công")
    @Operation(summary = "Create brand", description = "Tạo mới brand")
    public ResponseEntity<CreateBrandResponse> create(@RequestBody @Valid CreateBrandRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(brandService.createBrand(req));
    }

    @PutMapping("/brands")
    @ApiMessage(message = "Update brand thành công")
    @Operation(summary = "Update brand", description = "Update brand")
    public ResponseEntity<UpdateBrandResponse> update(@RequestBody @Valid UpdateBrandRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(brandService.updateBrand(req));
    }
}
