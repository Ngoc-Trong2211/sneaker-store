package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
//    @Query(value = "SELECT * FROM tbl_user WHERE email = :email", nativeQuery = true)
//    Optional<UserEntity> findByEmail(@Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, String id);

    Optional<UserEntity> findByRefreshTokenAndEmail(String refresh, String email);
}
