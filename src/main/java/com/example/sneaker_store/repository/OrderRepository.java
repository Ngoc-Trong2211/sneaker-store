package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {
    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId AND (:from IS NULL OR o.createdAt >= :from) AND (:to IS NULL OR o.createdAt <= :to) AND (:status IS NULL OR o.status = :status)")
    Page<OrderEntity> searchOrderByUser(@Param("userId") String userId,
                                        @Param("from") Instant from,
                                        @Param("to") Instant to,
                                        @Param("status") OrderStatus status,
                                        Pageable pageable);
    Optional<OrderEntity> findByCode(String code);

    @Query("""
    SELECT COUNT(o) > 0
    FROM Order o
    JOIN o.orderItems oi
    JOIN oi.productVariant pv
    WHERE o.user.id = :userId
    AND pv.product.id = :productId
    AND o.status = com.example.sneaker_store.util.enumEntity.OrderStatus.COMPLETED
""")
    boolean existsCompletedOrder(@Param("userId") String userId, @Param("productId") String productId);
}
