package com.example.sneaker_store.model;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.example.sneaker_store.util.enumEntity.VariantStatus;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_product_variant")
public class ProductVariantEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    private String color;
    private int stock;
    private String sku;

    @Enumerated(EnumType.STRING)
    private VariantStatus status;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CartItemEntity> cartItems;

    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItemEntity> orderItems;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductImageEntity> images;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductSizeEntity> sizes;

    @PrePersist
    public void create(){
        this.createdAt = Instant.now();
        this.createdBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        this.status = VariantStatus.ACTIVE;
    }

    @PreUpdate
    public void update(){
        this.updatedAt = Instant.now();
        this.updatedBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
    }
}
