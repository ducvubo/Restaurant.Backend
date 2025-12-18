package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    
    public static Specification<UserManagementJpaEntity> buildSpec(String keyword, Integer status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate usernamePredicate = builder.like(builder.lower(root.get("username")), likePattern);
                Predicate emailPredicate = builder.like(builder.lower(root.get("email")), likePattern);
                Predicate fullNamePredicate = builder.like(builder.lower(root.get("fullName")), likePattern);
                Predicate phonePredicate = builder.like(builder.lower(root.get("phone")), likePattern);
                Predicate addressPredicate = builder.like(builder.lower(root.get("address")), likePattern);
                
                predicates.add(builder.or(usernamePredicate, emailPredicate, fullNamePredicate, phonePredicate, addressPredicate));
            }
            
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
