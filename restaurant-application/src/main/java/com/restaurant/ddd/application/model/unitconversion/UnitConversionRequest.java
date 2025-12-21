package com.restaurant.ddd.application.model.unitconversion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UnitConversionRequest {
    private UUID fromUnitId;
    private UUID toUnitId;
    private BigDecimal conversionFactor;
    private String reason; // For audit log
}
