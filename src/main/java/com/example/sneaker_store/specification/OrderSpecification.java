package com.example.sneaker_store.specification;

import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<OrderEntity> specOrder(SpecificationOrderRequest req){
        return(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getKeySearch())){
                Predicate keyCode = criteriaBuilder.like(criteriaBuilder.upper(root.get("code")), "%" + req.getKeySearch().toUpperCase() + "%");
                Predicate keyName = criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + req.getKeySearch().toUpperCase() + "%");
                predicates.add(criteriaBuilder.or(keyCode, keyName));
            }
            if (hasText(req.getStatus())){
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(req.getStatus())));
            }
            if (hasText(req.getDateFrom())) {
                LocalDateTime fromDate = LocalDate.parse(req.getDateFrom()).atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),fromDate));
            }
            if (hasText(req.getDateTo())) {
                LocalDateTime toDate = LocalDate.parse(req.getDateTo()).atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
