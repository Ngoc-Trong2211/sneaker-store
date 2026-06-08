package com.example.sneaker_store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "tbl_favourite")
public class FavouriteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String guestId;
    private String productId;

    private Instant createdAt;

    @PrePersist
    public void create(){
        this.createdAt = Instant.now();
    }
}
