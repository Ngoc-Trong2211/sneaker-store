package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.SePayPaymentSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SePayPaymentSessionRepository extends JpaRepository<SePayPaymentSessionEntity, String> {
    Optional<SePayPaymentSessionEntity> findByPaymentCode(String paymentCode);
    boolean existsBySepayTransactionId(String sepayTransactionId);
}
