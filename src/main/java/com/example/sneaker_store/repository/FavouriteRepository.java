package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {
    boolean existsByUserIdAndProductId(String userId, String productId);
    boolean existsByGuestIdAndProductId(String guestId, String productId);
    void deleteByUserIdAndProductId(String userId, String productId);
    void deleteByGuestIdAndProductId(String guestId, String productId);
    @Query("SELECT f.productId FROM FavouriteEntity f WHERE f.userId = :userId")
    Set<String> findProductIdsByUserId(@Param("userId") String userId);

    @Query("SELECT f.productId FROM FavouriteEntity f WHERE f.guestId = :guestId")
    Set<String> findProductIdsByGuestId(@Param("guestId") String guestId);

    List<FavouriteEntity> findByGuestId(String guestId);
    void deleteByGuestId(String guestId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FavouriteEntity f WHERE f.guestId IS NOT NULL AND f.createdAt <= :expiredDate")
    void deleteExpiredGuestFavourite(Instant expiredDate);
}
