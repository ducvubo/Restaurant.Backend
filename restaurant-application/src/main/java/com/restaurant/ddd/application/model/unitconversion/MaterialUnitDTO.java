package com.restaurant.ddd.application.model.unitconversion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MaterialUnitDTO {
    private UUID id;
    private UUID materialId;
    private UUID unitId;
    private String unitName;
    private String unitSymbol;
    private Boolean isBaseUnit;
    private BigDecimal conversionFactor; // Conversion factor to base unit (null for base unit)
}
