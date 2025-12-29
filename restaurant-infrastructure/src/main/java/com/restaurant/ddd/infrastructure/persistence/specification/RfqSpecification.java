package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.infrastructure.persistence.entity.RfqJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification builder for RFQ queries
 */
public class RfqSpecification {
    
    public static Specification<RfqJpaEntity> buildSpec(
            String keyword,
            UUID supplierId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by keyword (rfq code, notes)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("rfqCode")), likePattern
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
            
            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Filter by date range
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sentDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sentDate"), toDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
