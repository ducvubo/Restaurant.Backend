package com.restaurant.ddd.infrastructure.persistence.specification;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.UnitConversionJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnitConversionSpecification {
    
    public static Specification<UnitConversionJpaEntity> buildSpec(
            Integer status,
            UUID fromUnitId,
            UUID toUnitId) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by status
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), DataStatus.fromCode(status)));
            }
            
            // Filter by fromUnitId
            if (fromUnitId != null) {
                predicates.add(builder.equal(root.get("fromUnitId"), fromUnitId));
            }
            
            // Filter by toUnitId
            if (toUnitId != null) {
                predicates.add(builder.equal(root.get("toUnitId"), toUnitId));
            }
            
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
