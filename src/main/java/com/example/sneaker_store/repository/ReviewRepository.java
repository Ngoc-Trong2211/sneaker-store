package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByUserIdAndProductId(String userId, String productId);
    List<ReviewEntity> findByProductId(String productId);
    @Query("SELECT COALESCE(AVG(r.star), 0) FROM ReviewEntity r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") String productId );


    @Query("""
        SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
        FROM OrderItemEntity oi
        JOIN oi.order o
        JOIN oi.productVariant pv
        WHERE o.code = :codeOrder
        AND pv.product.id = :productId
    """)
    boolean existsByOrderCodeAndProductId(
            @Param("codeOrder") String codeOrder,
            @Param("productId") String productId
    );
}
