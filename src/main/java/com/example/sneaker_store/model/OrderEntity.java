package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_order")
public class OrderEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    private String userId;
    private String guestId;

    private double subTotalAmount;
    private double couponDiscountAmount;
    private String couponCode;
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String address;
    private String phone;
    private String receiverName;
    private String code;
    private String email;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String lyDoHuy;
    private String nguoiHuy;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItemEntity> orderItems;

    @PrePersist
    public void create(){
        this.createdAt = Instant.now();
        this.createdBy = AuthServiceImpl.getCurrentUserLogin().orElse(null);
    }

    @PreUpdate
    public void update(){
        this.updatedAt = Instant.now();
        this.updatedBy = AuthServiceImpl.getCurrentUserLogin().orElse(null);
    }
}
