package com.example.sneaker_store.specification;

import com.example.sneaker_store.dto.request.review.SpecificationReviewRequest;
import com.example.sneaker_store.model.ReviewEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReviewSpecification {
    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static Specification<ReviewEntity> specReview(SpecificationReviewRequest req){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getPhone())){
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + req.getPhone() + "%"));
            }
            if (req.getStar() != null) {
                predicates.add(criteriaBuilder.equal(root.get("star"), req.getStar()));
            }
            if (hasText(req.getDateFrom())) {
                Instant fromDate = LocalDate.parse(req.getDateFrom()).atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),fromDate));
            }
            if (hasText(req.getDateTo())) {
                Instant toDate = LocalDate.parse(req.getDateTo()).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
