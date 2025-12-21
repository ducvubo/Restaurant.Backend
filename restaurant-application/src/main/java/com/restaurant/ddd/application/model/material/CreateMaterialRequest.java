package com.restaurant.ddd.application.model.material;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request for creating Material
 */
@Data
public class CreateMaterialRequest {
    private String code;
    private String name;
    private String category;
    private UUID categoryId;
    private BigDecimal unitPrice;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private String description;
}
