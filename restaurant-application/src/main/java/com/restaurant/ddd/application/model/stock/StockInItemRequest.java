package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request cho một dòng nguyên liệu trong phiếu nhập
 */
@Data
public class StockInItemRequest {
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private String notes;
}
