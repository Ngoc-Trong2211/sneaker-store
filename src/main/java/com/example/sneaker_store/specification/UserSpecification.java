package com.example.sneaker_store.specification;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.request.User.SpecificationUserRequest;
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
            if (hasText(req.getKeySearch())){
                Predicate keyName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + req.getKeySearch().toLowerCase() + "%");
                Predicate keyEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + req.getKeySearch().toLowerCase() + "%");
                Predicate keyPhone = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")),
                        "%" + req.getKeySearch().toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(keyEmail, keyName, keyPhone));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            if (hasText(req.getStatus())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")),
                        "%" + req.getStatus().toLowerCase() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("role").get("id"), req.getRoleId()));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
