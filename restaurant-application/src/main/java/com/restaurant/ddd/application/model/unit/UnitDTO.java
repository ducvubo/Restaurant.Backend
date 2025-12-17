package com.restaurant.ddd.application.model.unit;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Unit
 */
@Data
public class UnitDTO {
    private UUID id;
    private String code;
    private String name;
    private String symbol;
    private UUID baseUnitId;
    private String baseUnitName; // For display
    private BigDecimal conversionRate;
    private String description;
    private Integer status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
