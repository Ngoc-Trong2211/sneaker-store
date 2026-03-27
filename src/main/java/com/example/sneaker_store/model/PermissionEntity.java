package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.example.sneaker_store.util.enumEntity.MethodPermission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tbl_permission")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private String entity;

    @Enumerated(EnumType.STRING)
    private MethodPermission method;

    private String description;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<RoleEntity> roles;

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
