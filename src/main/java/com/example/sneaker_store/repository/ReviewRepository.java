package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByUserIdAndProductId(String userId, String productId);
}
