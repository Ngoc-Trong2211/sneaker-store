package com.example.sneaker_store.service.specification;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.request.brand.SpecificationBrandRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BrandSpecification {
    public static Specification<BrandEntity> specBrand(SpecificationBrandRequest req){
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(req.getKeyword())){
                Predicate keyName = criteriaBuilder.like(criteriaBuilder
                        .lower(root.get("name")), "%" + req.getKeyword().toLowerCase() + "%");
                Predicate keyCountry = criteriaBuilder.like(criteriaBuilder
                        .lower(root.get("country")), "%" + req.getKeyword().toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(keyName, keyCountry));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
            if (hasText(req.getName())){
                predicates.add(criteriaBuilder.like(criteriaBuilder
                        .lower(root.get("name")), "%" + req.getName().toLowerCase() + "%"));
            }
            if (hasText(req.getCountryCode())){
                predicates.add(criteriaBuilder.like(criteriaBuilder
                        .lower(root.get("countryCode")), "%" + req.getCountryCode().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }

    public static boolean hasText(String string){
        return string!=null && !string.trim().isEmpty();
    }
}
