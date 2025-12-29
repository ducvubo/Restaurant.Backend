package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.PurchasePriority;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PurchaseRequisition - Yêu cầu mua hàng
 * Domain model for purchase requisitions
 */
@Data
@Accessors(chain = true)
public class PurchaseRequisition {
    private UUID id;
    private String requisitionCode;
    private UUID warehouseId;
    private String warehouseName; // For display
    private UUID requestedBy;
    private String requestedByName; // For display
    private LocalDateTime requestDate;
    private LocalDateTime requiredDate;
    private PurchasePriority priority;
    private String notes;
    private PurchaseRequisitionStatus status;
    private UUID approvedBy;
    private String approvedByName; // For display
    private LocalDateTime approvedDate;
    private String rejectionReason;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    private List<PurchaseRequisitionItem> items = new ArrayList<>();

    /**
     * Validate requisition data
     */
    public void validate() {
        if (requisitionCode == null || requisitionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã yêu cầu mua hàng không được để trống");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Kho nhận hàng không được để trống");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Ngày yêu cầu không được để trống");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Yêu cầu mua hàng phải có ít nhất một mặt hàng");
        }
        // Validate each item
        for (PurchaseRequisitionItem item : items) {
            item.validate();
        }
    }

    /**
     * Submit for approval
     */
    public void submit() {
        if (this.status != PurchaseRequisitionStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể gửi phê duyệt yêu cầu ở trạng thái Nháp");
        }
        this.status = PurchaseRequisitionStatus.PENDING_APPROVAL;
    }

    /**
     * Approve requisition
     */
    public void approve(UUID approvedBy) {
        if (this.status != PurchaseRequisitionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Chỉ có thể phê duyệt yêu cầu đang chờ duyệt");
        }
        this.status = PurchaseRequisitionStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedDate = LocalDateTime.now();
    }

    /**
     * Reject requisition
     */
    public void reject(UUID rejectedBy, String reason) {
        if (this.status != PurchaseRequisitionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Chỉ có thể từ chối yêu cầu đang chờ duyệt");
        }
        this.status = PurchaseRequisitionStatus.REJECTED;
        this.approvedBy = rejectedBy;
        this.approvedDate = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Mark as converted to PO
     */
    public void markAsConverted() {
        if (this.status != PurchaseRequisitionStatus.APPROVED) {
            throw new IllegalStateException("Chỉ có thể chuyển đổi yêu cầu đã được phê duyệt");
        }
        this.status = PurchaseRequisitionStatus.CONVERTED;
    }

    /**
     * Cancel requisition
     */
    public void cancel() {
        if (this.status == PurchaseRequisitionStatus.CONVERTED || 
            this.status == PurchaseRequisitionStatus.CANCELLED) {
            throw new IllegalStateException("Không thể hủy yêu cầu đã chuyển đổi hoặc đã hủy");
        }
        this.status = PurchaseRequisitionStatus.CANCELLED;
    }

    /**
     * Check if can be edited
     */
    public boolean canEdit() {
        return this.status == PurchaseRequisitionStatus.DRAFT || 
               this.status == PurchaseRequisitionStatus.REJECTED;
    }

    /**
     * Check if can create RFQ
     */
    public boolean canCreateRfq() {
        return this.status == PurchaseRequisitionStatus.APPROVED;
    }
}
