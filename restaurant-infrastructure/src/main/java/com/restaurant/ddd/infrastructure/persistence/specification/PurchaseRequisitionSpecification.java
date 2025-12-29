package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.PurchaseRequisitionJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification builder for PurchaseRequisition queries
 */
public class PurchaseRequisitionSpecification {
    
    public static Specification<PurchaseRequisitionJpaEntity> buildSpec(
            String keyword,
            UUID warehouseId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by keyword (requisition code, notes)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("requisitionCode")), likePattern
                );
                Predicate notesPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("notes")), likePattern
                );
                predicates.add(criteriaBuilder.or(codePredicate, notesPredicate));
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
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("requestDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("requestDate"), toDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
