package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.coupon.CouponSpecificationRequest;
import com.example.sneaker_store.dto.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.dto.request.coupon.UpdateCouponRequest;
import com.example.sneaker_store.dto.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.GetCouponResponse;
import com.example.sneaker_store.dto.response.coupon.UpdateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.ValidateCouponResponse;
import com.example.sneaker_store.service.CouponService;
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

@RestController
@Slf4j(topic = "COUPON-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/coupon/v1")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/coupons")
    @Operation(summary = "Create coupon", description = "Create coupon")
    @ApiMessage(message = "Create coupon successfully")
    public ResponseEntity<CreateCouponResponse> createCoupon(@RequestBody @Valid CreateCouponRequest req){
        log.info("Create coupon");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.couponService.createCoupon(req));
    }

    @PutMapping("/coupons")
    @Operation(summary = "Update coupon", description = "Update coupon")
    @ApiMessage(message = "Update coupon successfully")
    public ResponseEntity<UpdateCouponResponse> updateCoupon(
            @RequestBody @Valid UpdateCouponRequest req
    ){
        log.info("Update coupon");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.couponService.updateCoupon(req));
    }

    @GetMapping("/coupons/{id}")
    @Operation(summary = "Get coupon by ID", description = "Get coupon by ID")
    @ApiMessage(message = "Get coupon successfully")
    public ResponseEntity<GetCouponResponse.Coupon> getCouponById(@PathVariable Long id) {
        log.info("Get coupon by id");
        return ResponseEntity.ok(this.couponService.getCouponById(id));
    }

    @GetMapping("/coupons")
    @Operation(summary = "Get coupons", description = "Get coupons with pagination and filtering")
    @ApiMessage(message = "Get coupons successfully")
    public ResponseEntity<GetCouponResponse> getCoupons(@ParameterObject Pageable pageable, CouponSpecificationRequest request) {
        log.info("Get coupons");
        return ResponseEntity.ok(this.couponService.getCoupons(request, pageable));
    }

    @GetMapping("/coupons/validate")
    @Operation(summary = "Validate coupon", description = "Validate coupon by code and order amount")
    @ApiMessage(message = "Validate coupon successfully")
    public ResponseEntity<ValidateCouponResponse> validateCoupon(
            @RequestParam String code,
            @RequestParam double orderAmount) {
        log.info("Validate coupon");
        return ResponseEntity.ok(this.couponService.validateCoupon(code, orderAmount));
    }

    @DeleteMapping("/coupons/{id}")
    @Operation(summary = "Delete coupon", description = "Delete coupon by ID")
    @ApiMessage(message = "Delete coupon successfully")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        log.info("Delete coupon");
        this.couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
