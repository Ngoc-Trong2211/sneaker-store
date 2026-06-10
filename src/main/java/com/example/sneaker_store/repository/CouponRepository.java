package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CouponEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long>, JpaSpecificationExecutor<CouponEntity> {
    Optional<CouponEntity> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponEntity c WHERE UPPER(c.code) = UPPER(:code)")
    Optional<CouponEntity> findByCodeForUpdate(@Param("code") String code);
}
