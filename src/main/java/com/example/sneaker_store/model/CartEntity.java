package com.example.sneaker_store.model;

import java.time.Instant;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import com.example.sneaker_store.service.impl.AuthServiceImpl;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_cart")
public class CartEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    private String guestId;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

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
