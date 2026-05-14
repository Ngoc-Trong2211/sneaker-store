package com.example.sneaker_store.model;

import com.example.sneaker_store.util.enumEntity.SizeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "tbl_product_size")
@Entity
public class ProductSizeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String size;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private SizeStatus status;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariantEntity variant;
}
