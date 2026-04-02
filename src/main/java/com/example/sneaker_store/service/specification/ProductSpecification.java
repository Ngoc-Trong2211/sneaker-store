package com.example.sneaker_store.service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.request.product.SpecificationProductRequest;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<ProductEntity> specDiscount(SpecificationProductRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            
            if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.between(root.get("price"), request.getMinPrice(), request.getMaxPrice()));
            } else if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            } else if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });   
    }
}
