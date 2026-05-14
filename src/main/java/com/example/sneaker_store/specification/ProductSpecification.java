package com.example.sneaker_store.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.dto.request.product.SpecificationProductRequest;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<ProductEntity> specProduct(SpecificationProductRequest request) {
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

            if (request.getBrandId() != null) predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), request.getBrandId()));
            if (request.getCategoryId() != null){
                Predicate equalId = criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId());
                Predicate equalParentId = criteriaBuilder.equal(root.get("category").get("parentId"), request.getCategoryId());
                predicates.add(criteriaBuilder.or(equalId, equalParentId));
            }

            if (request.getSlugCategory() != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("slug")),
                        "%" + request.getSlugCategory().toLowerCase() + "%"));
            }

            if (request.getBrands() != null && !request.getBrands().isEmpty()) {
                List<Predicate> brandPredicates = new ArrayList<>();
                for (String brand : request.getBrands()) {
                    brandPredicates.add(criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("brand").get("name")),
                                    "%" + brand.toLowerCase() + "%"
                            )
                    );
                }
                predicates.add(criteriaBuilder.or(brandPredicates.toArray(new Predicate[0])));
            }

            if (request.getSizes() != null && !request.getSizes().isEmpty()) {
                Objects.requireNonNull(query).distinct(true);
                Join<Object, Object> variantJoin = root.join("variants", JoinType.LEFT);
                Join<Object, Object> sizeJoin = variantJoin.join("sizes", JoinType.LEFT);
                List<Predicate> sizePredicates = new ArrayList<>();

                for (String size : request.getSizes()) {
                    sizePredicates.add(criteriaBuilder.equal(sizeJoin.get("size"),size));
                }

                predicates.add(criteriaBuilder.or(sizePredicates.toArray(new Predicate[0])));
                predicates.add(criteriaBuilder.greaterThan(sizeJoin.get("quantity"),0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });   
    }
}
