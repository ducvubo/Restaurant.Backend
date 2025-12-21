package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "unit_conversion_history")
public class UnitConversionHistoryJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "unit_conversion_id", nullable = false, columnDefinition = "UUID")
    private UUID unitConversionId;

    @Column(name = "from_unit_id", nullable = false, columnDefinition = "UUID")
    private UUID fromUnitId;

    @Column(name = "to_unit_id", nullable = false, columnDefinition = "UUID")
    private UUID toUnitId;

    @Column(name = "old_factor", precision = 18, scale = 6)
    private BigDecimal oldFactor;

    @Column(name = "new_factor", nullable = false, precision = 18, scale = 6)
    private BigDecimal newFactor;

    @Column(name = "change_type", length = 20, nullable = false)
    private String changeType; // CREATE, UPDATE, DELETE

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_by", nullable = false, columnDefinition = "UUID")
    private UUID changedBy;

    @Column(name = "changed_date", nullable = false)
    private LocalDateTime changedDate = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (changedDate == null) {
            changedDate = LocalDateTime.now();
        }
    }
}
