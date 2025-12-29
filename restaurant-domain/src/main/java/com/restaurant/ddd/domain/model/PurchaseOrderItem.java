package com.restaurant.ddd.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PurchaseOrderItem - Chi tiết đơn đặt hàng
 */
@Data
@Accessors(chain = true)
public class PurchaseOrderItem {
    private UUID id;
    private UUID poId;
    private UUID materialId;
    private String materialCode; // For display
    private String materialName; // For display
    private BigDecimal quantity;
    private BigDecimal receivedQuantity; // Số lượng đã nhận
    private UUID unitId;
    private String unitName; // For display
    private BigDecimal unitPrice;
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
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm");
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

    /**
     * Receive quantity
     */
    public void receiveQuantity(BigDecimal quantity) {
        if (this.receivedQuantity == null) {
            this.receivedQuantity = BigDecimal.ZERO;
        }
        this.receivedQuantity = this.receivedQuantity.add(quantity);
    }

    /**
     * Get remaining quantity
     */
    public BigDecimal getRemainingQuantity() {
        if (this.quantity == null) return BigDecimal.ZERO;
        if (this.receivedQuantity == null) return this.quantity;
        return this.quantity.subtract(this.receivedQuantity);
    }

    /**
     * Check if fully received
     */
    public boolean isFullyReceived() {
        return getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0;
    }
}
