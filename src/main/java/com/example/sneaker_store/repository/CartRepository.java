package com.example.sneaker_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sneaker_store.model.CartEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, String>{
    Optional<CartEntity> findByGuestId(String id);
    Optional<CartEntity> findByUserId(String id);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartEntity c WHERE c.guestId IS NOT NULL AND c.createdAt <= :expiredDate")
    void deleteExpiredGuestCart(Instant expiredDate);
}
