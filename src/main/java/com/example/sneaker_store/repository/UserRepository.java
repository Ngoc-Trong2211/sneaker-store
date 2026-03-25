package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
