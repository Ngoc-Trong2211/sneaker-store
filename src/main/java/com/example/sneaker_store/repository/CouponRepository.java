package com.example.sneaker_store.repository;

import com.example.sneaker_store.service.CouponService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponService, Long> {
}
