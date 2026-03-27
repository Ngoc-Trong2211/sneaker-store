package com.example.sneaker_store.model;

import com.example.sneaker_store.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tbl_role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbl_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @JsonIgnoreProperties(value = {"roles"})
    private List<PermissionEntity> permissions;

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
