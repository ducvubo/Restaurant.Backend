package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.RfqStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RequestForQuotation - Yêu cầu báo giá
 * Domain model for RFQ
 */
@Data
@Accessors(chain = true)
public class RequestForQuotation {
    private UUID id;
    private String rfqCode;
    private UUID requisitionId;
    private String requisitionCode; // For display
    private UUID supplierId;
    private String supplierName; // For display
    private LocalDateTime sentDate;
    private LocalDateTime validUntil;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private String deliveryTerms;
    private String notes;
    private RfqStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private List<RfqItem> items = new ArrayList<>();

    /**
     * Validate RFQ data
     */
    public void validate() {
        if (rfqCode == null || rfqCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã báo giá không được để trống");
        }
        if (supplierId == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Báo giá phải có ít nhất một mặt hàng");
        }
    }

    /**
     * Send to supplier
     */
    public void send() {
        if (this.status != RfqStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể gửi báo giá ở trạng thái Nháp");
        }
        this.status = RfqStatus.SENT;
        this.sentDate = LocalDateTime.now();
    }

    /**
     * Receive quotation from supplier
     */
    public void receiveQuotation() {
        if (this.status != RfqStatus.SENT) {
            throw new IllegalStateException("Chỉ có thể nhận báo giá đã gửi");
        }
        this.status = RfqStatus.RECEIVED;
    }

    /**
     * Accept quotation
     */
    public void accept() {
        if (this.status != RfqStatus.RECEIVED) {
            throw new IllegalStateException("Chỉ có thể chấp nhận báo giá đã nhận");
        }
        this.status = RfqStatus.ACCEPTED;
    }

    /**
     * Reject quotation
     */
    public void reject() {
        if (this.status != RfqStatus.RECEIVED && this.status != RfqStatus.SENT) {
            throw new IllegalStateException("Không thể từ chối báo giá ở trạng thái này");
        }
        this.status = RfqStatus.REJECTED;
    }

    /**
     * Mark as expired
     */
    public void markAsExpired() {
        if (this.status == RfqStatus.ACCEPTED || this.status == RfqStatus.CANCELLED) {
            return; // Don't change if already finalized
        }
        this.status = RfqStatus.EXPIRED;
    }

    /**
     * Cancel RFQ
     */
    public void cancel() {
        if (this.status == RfqStatus.ACCEPTED) {
            throw new IllegalStateException("Không thể hủy báo giá đã chấp nhận");
        }
        this.status = RfqStatus.CANCELLED;
    }

    /**
     * Calculate total amount
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(RfqItem::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if can create PO
     */
    public boolean canCreatePo() {
        return this.status == RfqStatus.ACCEPTED;
    }

    /**
     * Check if quotation is expired
     */
    public boolean isExpired() {
        return this.validUntil != null && LocalDateTime.now().isAfter(this.validUntil);
    }
}
