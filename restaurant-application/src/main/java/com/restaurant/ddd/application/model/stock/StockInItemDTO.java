package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO cho chi tiết phiếu nhập kho
 */
@Data
public class StockInItemDTO {
    private UUID id;
    private UUID materialId;
    private String materialName;      // Tên nguyên liệu (join)
    private UUID unitId;
    private String unitName;          // Tên đơn vị (join)
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String notes;
}
