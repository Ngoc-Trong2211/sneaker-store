package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "tbl_order_item")
public class OrderItemEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    private int quantity;
    private double price;
    private String productName;
    private String size;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private String productId;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariantEntity productVariant;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

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
