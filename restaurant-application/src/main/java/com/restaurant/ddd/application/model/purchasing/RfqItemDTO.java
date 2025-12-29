package com.restaurant.ddd.application.model.purchasing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for RfqItem
 */
@Data
public class RfqItemDTO {
    private UUID id;
    private UUID rfqId;
    private UUID materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal quantity;
    private UUID unitId;
    private String unitName;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String notes;
}
