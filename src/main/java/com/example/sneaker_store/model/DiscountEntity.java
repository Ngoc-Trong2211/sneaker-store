package com.example.sneaker_store.model;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.example.sneaker_store.util.enumEntity.DiscountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_discount")
public class DiscountEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    private int percent;
    private String description;
    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private DiscountStatus status;

    private String applyFor;
    private String nameApply;

    private Instant createdAt;
    private String createdBy;  
    private Instant updatedAt;
    private String updatedBy;

    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductEntity> products;

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
