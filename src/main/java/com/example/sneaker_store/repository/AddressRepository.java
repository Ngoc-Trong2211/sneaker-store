package com.example.sneaker_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sneaker_store.model.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    
}
