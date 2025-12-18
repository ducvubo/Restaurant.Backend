package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UnitSpecification {
    
    public static Specification<UnitJpaEntity> buildSpec(String keyword, Integer status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate codePredicate = builder.like(builder.lower(root.get("code")), likePattern);
                Predicate namePredicate = builder.like(builder.lower(root.get("name")), likePattern);
                Predicate symbolPredicate = builder.like(builder.lower(root.get("symbol")), likePattern);
                
                predicates.add(builder.or(codePredicate, namePredicate, symbolPredicate));
            }
            
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
