package com.example.sneaker_store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.sneaker_store.model.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    @Query("SELECT a FROM AddressEntity a WHERE a.user.id = :userId")
    List<AddressEntity> findByUserId(String userId);

    boolean existsByWardAndAddressLineAndCityAndUserId(String ward, String addressLine, String city, String userId);

    AddressEntity findByUserIdAndIsDefault(String userId, boolean isDefault);
}
