package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PurchaseRequisitionItem - Chi tiết yêu cầu mua hàng
 */
@Data
@Accessors(chain = true)
public class PurchaseRequisitionItem {
    private UUID id;
    private UUID requisitionId;
    private UUID materialId;
    private String materialCode; // For display
    private String materialName; // For display
    private BigDecimal quantity;
    private UUID unitId;
    private String unitName; // For display
    private BigDecimal estimatedPrice; // Giá ước tính
    private BigDecimal estimatedAmount; // Thành tiền ước tính
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
     * Calculate estimated amount
     */
    public void calculateEstimatedAmount() {
        if (quantity != null && estimatedPrice != null) {
            this.estimatedAmount = quantity.multiply(estimatedPrice);
        }
    }
}
