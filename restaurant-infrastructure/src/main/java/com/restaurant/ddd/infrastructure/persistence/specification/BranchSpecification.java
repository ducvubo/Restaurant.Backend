package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.BranchJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BranchSpecification {
    
    public static Specification<BranchJpaEntity> buildSpec(String keyword, Integer status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = builder.like(builder.lower(root.get("name")), likePattern);
                Predicate codePredicate = builder.like(builder.lower(root.get("code")), likePattern);
                Predicate emailPredicate = builder.like(builder.lower(root.get("email")), likePattern);
                Predicate phonePredicate = builder.like(builder.lower(root.get("phone")), likePattern);
                Predicate addressPredicate = builder.like(builder.lower(root.get("address")), likePattern);
                
                predicates.add(builder.or(namePredicate, codePredicate, emailPredicate, phonePredicate, addressPredicate));
            }
            
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
