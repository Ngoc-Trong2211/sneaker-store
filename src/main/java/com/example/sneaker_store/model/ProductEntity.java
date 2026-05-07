package com.example.sneaker_store.model;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.example.sneaker_store.util.SlugUtil;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_product")
public class ProductEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private double price;
    private String slug;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private Instant createdAt;
    private String createdBy;  
    private Instant updatedAt;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private DiscountEntity discount;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductVariantEntity> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductImageEntity> images;

    @PrePersist
    public void create(){
        this.createdAt = Instant.now();
        this.createdBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        if (this.slug == null || this.slug.trim().isEmpty()) {
            this.slug = SlugUtil.toSlug(this.name);
        }
    }

    @PreUpdate
    public void update(){
        this.updatedAt = Instant.now();
        this.updatedBy = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        this.slug = SlugUtil.toSlug(this.name);
    }
}
