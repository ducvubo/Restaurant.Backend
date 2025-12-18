package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.WarehouseJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification builder for Warehouse queries
 */
public class WarehouseSpecification {
    
    public static Specification<WarehouseJpaEntity> buildSpec(
            String keyword,
            Integer status,
            UUID branchId,
            Integer warehouseType) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by keyword (name or code)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), likePattern
                );
                Predicate codePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("code")), likePattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, codePredicate));
            }
            
            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Filter by branch
            if (branchId != null) {
                predicates.add(criteriaBuilder.equal(root.get("branchId"), branchId));
            }
            
            // Filter by warehouse type
            if (warehouseType != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseType"), warehouseType));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
