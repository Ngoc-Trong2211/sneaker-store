package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_sepay_payment_session")
public class SePayPaymentSessionEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String paymentCode;

    private String status;
    private String cartId;
    private String userId;
    private String guestId;
    private String email;
    private String phone;
    private String receiverName;
    private String address;
    private String couponCode;
    private double subTotalAmount;
    private double couponDiscountAmount;
    private double totalAmount;
    private String orderId;

    @Column(unique = true)
    private String sepayTransactionId;

    private Instant paidAt;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "paymentSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SePayPaymentSessionItemEntity> items;

    @PrePersist
    public void create() {
        this.createdAt = Instant.now();
        this.createdBy = AuthServiceImpl.getCurrentUserLogin().orElse(null);
    }

    @PreUpdate
    public void update() {
        this.updatedAt = Instant.now();
        this.updatedBy = AuthServiceImpl.getCurrentUserLogin().orElse(null);
    }
}
