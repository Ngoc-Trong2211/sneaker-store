package com.example.sneaker_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_brand")
@Getter
@Setter
public class BrandEntity {
    private Long id;
    private String name;
}
