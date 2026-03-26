package com.example.sneaker_store.service.specification;

import jakarta.persistence.criteria.Predicate;
import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.model.request.permssion.PermissionSpecificationRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PermissionSpecification {
    public static boolean hasText(String string){
        return string!=null && !string.trim().isEmpty();
    }

    public static Specification<PermissionEntity> specPermission(PermissionSpecificationRequest req){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getEntity())){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("entity")),
                        "%" + req.getEntity().toLowerCase() + "%"));
            }
            if (req.getMethod()!=null){
                predicates.add(criteriaBuilder.equal(root.get("method"), req.getMethod()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
