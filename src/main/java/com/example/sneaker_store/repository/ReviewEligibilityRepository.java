package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ReviewEligibilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewEligibilityRepository extends JpaRepository<ReviewEligibilityEntity, String> {
    boolean existsByOrderItemId(String orderItemId);

    boolean existsByOrderIdAndProductIdAndStatusFalse(String orderId, String productId);

    Optional<ReviewEligibilityEntity> findByOrderItemId(String orderItemId);

    Optional<ReviewEligibilityEntity> findByOrderItemIdAndStatusFalse(String orderItemId);

    Optional<ReviewEligibilityEntity> findFirstByUserIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
            String userId,
            String productId
    );

    Optional<ReviewEligibilityEntity> findFirstByOrderIdAndProductIdAndStatusFalseOrderByCreatedAtAsc(
            String orderId,
            String productId
    );
}
