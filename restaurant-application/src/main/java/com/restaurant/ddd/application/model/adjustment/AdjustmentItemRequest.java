package com.restaurant.ddd.application.model.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AdjustmentItemRequest {
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private String notes;
}
