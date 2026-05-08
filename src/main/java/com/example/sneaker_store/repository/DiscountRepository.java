package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.DiscountEntity;

import com.example.sneaker_store.util.enumEntity.DiscountStatus;
import jakarta.transaction.Transactional;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountRepository extends JpaRepository<DiscountEntity, String>, JpaSpecificationExecutor<DiscountEntity> {
    @Query("""
        SELECT COUNT(d) > 0
                FROM DiscountEntity d
                        WHERE d.applyFor = :applyFor
                                 AND d.nameApply = :nameApply
                                 AND d.status = 'ACTIVE'
                                 AND d.endTime >= :startTime
                                 AND d.startTime <= :endTime
        """)
    boolean existsOverlap(@Param("applyFor") String applyFor,
                          @Param("nameApply") String nameApply,
                          @Param("endTime") Instant endTime,
                          @Param("startTime") Instant startTime);

    @Modifying
    @Transactional
    @Query("UPDATE DiscountEntity d SET d.status = 'EXPIRED' WHERE d.endTime < :currentTime AND d.status != 'EXPIRED'")
    void updateExpiredDiscounts(@Param("currentTime") Instant currentTime);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DiscountEntity d WHERE d.id = :id AND d.endTime < :currentTime")
    boolean checkEndTimeBeforeCurrentTime(@Param("id") String id, @Param("currentTime") Instant currentTime);
}
