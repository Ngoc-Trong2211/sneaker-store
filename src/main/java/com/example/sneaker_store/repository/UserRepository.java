package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
//    @Query(value = "SELECT * FROM tbl_user WHERE email = :email", nativeQuery = true)
//    Optional<UserEntity> findByEmail(@Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
