package com.restaurant.ddd.application.model.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO cho chi tiết phiếu xuất kho
 */
@Data
public class StockOutItemDTO {
    private UUID id;
    private UUID materialId;
    private String materialName;      // Tên nguyên liệu (join)
    private UUID unitId;
    private String unitName;          // Tên đơn vị (join)
    private BigDecimal quantity;
    private BigDecimal unitPrice;     // Optional
    private BigDecimal totalAmount;   // Optional
    private String notes;
    private List<StockOutBatchMappingDTO> batchMappings;  // Truy vết: lấy từ batch nào
}
