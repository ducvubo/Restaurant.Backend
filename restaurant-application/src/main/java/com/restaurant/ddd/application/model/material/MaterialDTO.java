package com.restaurant.ddd.application.model.material;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Material
 */
@Data
public class MaterialDTO {
    private UUID id;
    private String code;
    private String name;
    private String category;
    private UUID categoryId;
    private String categoryName; // For display
    private UUID unitId;
    private String unitName; // For display
    private BigDecimal unitPrice;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private String description;
    private Integer status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
