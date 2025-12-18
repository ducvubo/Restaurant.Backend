package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.SupplierJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification builder for Supplier queries
 */
public class SupplierSpecification {
    
    public static Specification<SupplierJpaEntity> buildSpec(
            String keyword,
            Integer status) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by keyword (name, phone, email)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), likePattern
                );
                Predicate phonePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("phone")), likePattern
                );
                Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), likePattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, phonePredicate, emailPredicate));
            }
            
            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
