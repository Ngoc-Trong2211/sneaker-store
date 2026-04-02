package com.example.sneaker_store.service.specification;

import java.util.ArrayList;
import java.util.List;


import org.springframework.data.jpa.domain.Specification;

import com.example.sneaker_store.model.DiscountEntity;
import com.example.sneaker_store.model.request.discount.DiscountSpecificationRequest;

import jakarta.persistence.criteria.Predicate;

public class DiscountSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<DiscountEntity> specDiscount(DiscountSpecificationRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(request.getNameApply())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameApply")), "%" + request.getNameApply().toLowerCase() + "%"));
            }
            if (hasText(request.getApplyFor())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("applyFor")), "%" + request.getApplyFor().toLowerCase() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });   
    }
}
