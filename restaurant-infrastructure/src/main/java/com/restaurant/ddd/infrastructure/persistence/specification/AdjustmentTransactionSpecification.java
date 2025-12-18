package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.AdjustmentTransactionJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdjustmentTransactionSpecification {
    
    public static Specification<AdjustmentTransactionJpaEntity> buildSpec(
            UUID warehouseId,
            UUID materialId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Integer status) {
        
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (warehouseId != null) {
                predicates.add(builder.equal(root.get("warehouseId"), warehouseId));
            }
            
            if (materialId != null) {
                predicates.add(builder.equal(root.get("materialId"), materialId));
            }
            
            if (fromDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("createdDate"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("createdDate"), toDate));
            }
            
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
