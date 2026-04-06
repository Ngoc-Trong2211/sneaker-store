package com.example.sneaker_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sneaker_store.model.CartEntity;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, String>{
    Optional<CartEntity> findByGuestId(String id);
    Optional<CartEntity> findByUserId(String id);
}
