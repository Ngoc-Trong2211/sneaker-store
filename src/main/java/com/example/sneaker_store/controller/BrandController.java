package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.brand.CreateBrandRequest;
import com.example.sneaker_store.dto.request.brand.SpecificationBrandRequest;
import com.example.sneaker_store.dto.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.dto.response.brand.CreateBrandResponse;
import com.example.sneaker_store.dto.response.brand.GetBrandResponse;
import com.example.sneaker_store.dto.response.brand.UpdateBrandResponse;
import com.example.sneaker_store.service.BrandService;
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

import java.net.URISyntaxException;
import java.util.List;

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
        log.info("CREATE BRAND");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.brandService.createBrand(req));
    }

    @PutMapping("/brands")
    @ApiMessage(message = "Update brand thành công")
    @Operation(summary = "Update brand", description = "Update brand")
    public ResponseEntity<UpdateBrandResponse> update(@RequestBody @Valid UpdateBrandRequest req) throws URISyntaxException {
        log.info("UPDATE BRAND");
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(this.brandService.updateBrand(req));
    }

    @GetMapping("/brands")
    @ApiMessage(message = "Get brand thành công")
    @Operation(summary = "Get brand", description = "Get brand")
    public ResponseEntity<GetBrandResponse> get(@ParameterObject Pageable pageable,
                                                SpecificationBrandRequest request) {
        log.info("GET LIST BRAND");
        return ResponseEntity.ok(this.brandService.getBrand(pageable, request));
    }

    @GetMapping("/brands/all")
    @ApiMessage(message = "Get brand thành công")
    @Operation(summary = "Get brand", description = "Get brand")
    public ResponseEntity<List<GetBrandResponse.Brand>> getAll() {
        log.info("GET LIST BRANDS");
        return ResponseEntity.ok(this.brandService.getAll());
    }

    @DeleteMapping("/brands/{id}")
    @ApiMessage(message = "Delete brand thành công")
    @Operation(summary = "Delete brand", description = "Delete brand")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws URISyntaxException {
        log.info("DELETE BRAND");
        this.brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
