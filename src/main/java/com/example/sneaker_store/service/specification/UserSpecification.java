package com.example.sneaker_store.service.specification;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.SpecificationUserRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static boolean hasText(String text){
        return text!=null && !text.trim().isEmpty();
    }

    public static Specification<UserEntity> specUser(SpecificationUserRequest req){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getEmail())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + req.getEmail().toLowerCase() + "%"));
            }
            if (hasText(req.getName())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + req.getName().toLowerCase() + "%"));
            }
            if (hasText(req.getPhone())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")),
                        "%" + req.getPhone().toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
