package com.example.sneaker_store.service.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.model.request.productVariant.SpecificationProductVariantRequest;

import jakarta.persistence.criteria.Predicate;

public class ProductVariantSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<ProductVariantEntity> specVariant(SpecificationProductVariantRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(request.getSize())) {
                predicates.add(criteriaBuilder.equal(root.get("size"), request.getSize()));
            }

            if (hasText(request.getColor())) {
                predicates.add(criteriaBuilder.equal(root.get("color"), request.getColor()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
