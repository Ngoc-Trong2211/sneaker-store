package com.example.sneaker_store.specification;

import com.example.sneaker_store.dto.request.category.SpecificationCategoryRequest;
import com.example.sneaker_store.model.CategoryEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<CategoryEntity> specCategory(SpecificationCategoryRequest req){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getName())){
                Predicate keyName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + req.getName().toLowerCase() + "%");
                Predicate keySlug = criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), "%" + req.getName().toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(keyName, keySlug));
            }
            if (hasText(req.getType())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("type")), "%" + req.getType() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }

    public static boolean hasText(String string){
        return string!=null && !string.trim().isEmpty();
    }
}
