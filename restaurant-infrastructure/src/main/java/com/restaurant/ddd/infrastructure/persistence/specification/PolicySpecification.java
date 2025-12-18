package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.PolicyJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PolicySpecification {
    
    public static Specification<PolicyJpaEntity> buildSpec(String keyword, Integer status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = builder.like(builder.lower(root.get("name")), likePattern);
                Predicate descriptionPredicate = builder.like(builder.lower(root.get("description")), likePattern);
                
                predicates.add(builder.or(namePredicate, descriptionPredicate));
            }
            
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
