package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PurchaseOrder - Đơn đặt hàng mua
 * Domain model for purchase orders
 */
@Data
@Accessors(chain = true)
public class PurchaseOrder {
    private UUID id;
    private String poCode;
    private UUID rfqId;
    private String rfqCode; // For display
    private UUID supplierId;
    private String supplierName; // For display
    private UUID warehouseId;
    private String warehouseName; // For display
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private String paymentTerms;
    private String deliveryTerms;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount; // Giá trị đã nhận (tính theo tiền)
    private String notes;
    private PurchaseOrderStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private List<PurchaseOrderItem> items = new ArrayList<>();

    /**
     * Validate PO data
     */
    public void validate() {
        if (poCode == null || poCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống");
        }
        if (supplierId == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Kho nhận hàng không được để trống");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("Ngày đặt hàng không được để trống");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một mặt hàng");
        }
        // Validate each item
        for (PurchaseOrderItem item : items) {
            item.validate();
        }
    }

    /**
     * Confirm PO with supplier
     */
    public void confirm() {
        if (this.status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể xác nhận đơn hàng ở trạng thái Nháp");
        }
        this.status = PurchaseOrderStatus.CONFIRMED;
    }

    /**
     * Receive goods (partial or full)
     */
    public void receiveGoods(BigDecimal receivedValue) {
        if (this.status != PurchaseOrderStatus.CONFIRMED && 
            this.status != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Chỉ có thể nhận hàng khi đơn hàng đã xác nhận");
        }
        
        this.receivedAmount = (this.receivedAmount != null ? this.receivedAmount : BigDecimal.ZERO)
                .add(receivedValue);
        
        // Check if fully received
        if (this.receivedAmount.compareTo(this.totalAmount) >= 0) {
            this.status = PurchaseOrderStatus.COMPLETED;
        } else {
            this.status = PurchaseOrderStatus.PARTIALLY_RECEIVED;
        }
    }

    /**
     * Cancel PO
     */
    public void cancel() {
        if (this.status == PurchaseOrderStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã hoàn thành");
        }
        if (this.status == PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã nhận một phần. Vui lòng tạo phiếu trả hàng");
        }
        this.status = PurchaseOrderStatus.CANCELLED;
    }

    /**
     * Calculate total amount
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(PurchaseOrderItem::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if can receive goods
     */
    public boolean canReceiveGoods() {
        return this.status == PurchaseOrderStatus.CONFIRMED || 
               this.status == PurchaseOrderStatus.PARTIALLY_RECEIVED;
    }

    /**
     * Check if can be edited
     */
    public boolean canEdit() {
        return this.status == PurchaseOrderStatus.DRAFT;
    }

    /**
     * Get remaining amount to receive
     */
    public BigDecimal getRemainingAmount() {
        if (this.totalAmount == null) return BigDecimal.ZERO;
        if (this.receivedAmount == null) return this.totalAmount;
        return this.totalAmount.subtract(this.receivedAmount);
    }

    /**
     * Get receiving progress percentage
     */
    public BigDecimal getReceivingProgress() {
        if (this.totalAmount == null || this.totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (this.receivedAmount == null) return BigDecimal.ZERO;
        return this.receivedAmount.divide(this.totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
