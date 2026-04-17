package com.example.sneaker_store.specification;

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
                Predicate keyName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
                Predicate keySlug = criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), "%" + name.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(keyName, keySlug));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }

    public static boolean hasText(String string){
        return string!=null && !string.trim().isEmpty();
    }
}
