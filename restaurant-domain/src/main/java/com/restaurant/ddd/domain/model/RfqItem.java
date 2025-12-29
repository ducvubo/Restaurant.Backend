package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * RfqItem - Chi tiết yêu cầu báo giá
 */
@Data
@Accessors(chain = true)
public class RfqItem {
    private UUID id;
    private UUID rfqId;
    private UUID materialId;
    private String materialCode; // For display
    private String materialName; // For display
    private BigDecimal quantity;
    private UUID unitId;
    private String unitName; // For display
    private BigDecimal unitPrice; // Đơn giá từ NCC
    private BigDecimal amount; // Thành tiền
    private String notes;

    /**
     * Validate item data
     */
    public void validate() {
        if (materialId == null) {
            throw new IllegalArgumentException("Nguyên vật liệu không được để trống");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (unitId == null) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
    }

    /**
     * Calculate amount
     */
    public void calculateAmount() {
        if (quantity != null && unitPrice != null) {
            this.amount = quantity.multiply(unitPrice);
        }
    }
}
