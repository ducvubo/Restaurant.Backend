package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.InventoryLedgerJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification builder for Inventory Ledger queries
 */
public class InventoryLedgerSpecification {
    
    /**
     * Build specification for Inventory Ledger
     */
    public static Specification<InventoryLedgerJpaEntity> buildSpec(
            UUID warehouseId,
            UUID materialId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by warehouse
            if (warehouseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseId"), warehouseId));
            }
            
            // Filter by material
            if (materialId != null) {
                predicates.add(criteriaBuilder.equal(root.get("materialId"), materialId));
            }
            
            // Filter by date range
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("transactionDate"), startDate
                ));
            }
            
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("transactionDate"), endDate
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
