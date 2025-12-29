package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseOrderJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification builder for PurchaseOrder queries
 */
public class PurchaseOrderSpecification {
    
    public static Specification<PurchaseOrderJpaEntity> buildSpec(
            String keyword,
            UUID supplierId,
            UUID warehouseId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by keyword (po code, notes)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("poCode")), likePattern
                );
                Predicate notesPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("notes")), likePattern
                );
                predicates.add(criteriaBuilder.or(codePredicate, notesPredicate));
            }
            
            // Filter by supplier
            if (supplierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("supplierId"), supplierId));
            }
            
            // Filter by warehouse
            if (warehouseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseId"), warehouseId));
            }
            
            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Filter by date range
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), toDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
