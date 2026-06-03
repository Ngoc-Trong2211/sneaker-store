package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {
    boolean existsByUserIdAndProductId(String userId, String productId);
    boolean existsByGuestIdAndProductId(String guestId, String productId);
}
