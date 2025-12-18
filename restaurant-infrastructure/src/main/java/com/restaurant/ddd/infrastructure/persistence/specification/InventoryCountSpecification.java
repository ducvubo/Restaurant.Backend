package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryCountStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryCountSpecification {
    
    public static Specification<InventoryCountJpaEntity> withFilters(
            UUID warehouseId,
            InventoryCountStatus countStatus,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            DataStatus status) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (warehouseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseId"), warehouseId));
            }
            
            if (countStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("countStatus"), countStatus));
            }
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("countDate"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("countDate"), toDate));
            }
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            } else {
                // Default: exclude deleted
                predicates.add(criteriaBuilder.notEqual(root.get("status"), DataStatus.DELETED));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
