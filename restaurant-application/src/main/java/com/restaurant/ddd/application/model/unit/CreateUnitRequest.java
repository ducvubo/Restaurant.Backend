package com.restaurant.ddd.application.model.unit;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request for creating Unit
 */
@Data
public class CreateUnitRequest {
    private String code;
    private String name;
    private String symbol;
    private UUID baseUnitId;
    private BigDecimal conversionRate;
    private String description;
}
