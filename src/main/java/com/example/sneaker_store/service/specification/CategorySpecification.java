package com.example.sneaker_store.service.specification;

import com.example.sneaker_store.model.CategoryEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<CategoryEntity> specCategory(String name){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(name)){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }

    public static boolean hasText(String string){
        return string!=null && !string.trim().isEmpty();
    }
}
