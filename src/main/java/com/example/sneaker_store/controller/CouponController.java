package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.model.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.service.CouponService;
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
@Slf4j(topic = "COUPON-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/coupon/v1")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/coupons")
    @Operation(summary = "Create coupon", description = "create coupon")
    @ApiMessage(message = "Create coupon successfully")
    public ResponseEntity<CreateCouponResponse> createCoupon(@RequestBody @Valid CreateCouponRequest req){
        log.info("Create coupon");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.couponService.createCoupon(req));
    }
}
