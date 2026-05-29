package com.example.sneaker_store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "tbl_review_eligibility",
        uniqueConstraints = @UniqueConstraint(columnNames = "order_item_id")
)
public class ReviewEligibilityEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "order_item_id", nullable = false)
    private String orderItemId;

    @Column(name = "review_id")
    private Long reviewId;

    private boolean status = false;

    private Instant createdAt;

    @PrePersist
    public void create() {
        this.createdAt = Instant.now();
    }
}
