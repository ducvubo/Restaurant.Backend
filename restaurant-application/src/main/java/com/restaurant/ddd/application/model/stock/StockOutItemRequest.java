package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request cho một dòng nguyên liệu trong phiếu xuất
 */
@Data
public class StockOutItemRequest {
    private UUID materialId;
    private UUID unitId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;      // Giá bán (cho bán lẻ)
    private BigDecimal totalAmount;    // Thành tiền (cho bán lẻ)
    private String notes;
}
