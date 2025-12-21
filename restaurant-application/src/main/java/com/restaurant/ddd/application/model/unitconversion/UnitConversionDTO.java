package com.restaurant.ddd.application.model.unitconversion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UnitConversionDTO {
    private UUID id;
    private UUID fromUnitId;
    private String fromUnitName;
    private String fromUnitSymbol;
    private UUID toUnitId;
    private String toUnitName;
    private String toUnitSymbol;
    private BigDecimal conversionFactor;
    private String status;
    private Long usageCount; // Number of transactions using this conversion
}
