package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "tbl_cart_item")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariantEntity productVariant;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @PrePersist
    public void create(){
        this.createdAt = Instant.now();
        this.createdBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
    }

    @PreUpdate
    public void update(){
        this.updatedAt = Instant.now();
        this.updatedBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
    }
}
