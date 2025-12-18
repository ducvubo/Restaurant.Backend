package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.InventoryCountStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * InventoryCount - Phiếu kiểm kê kho
 * Dùng để ghi nhận kết quả kiểm đếm thực tế tồn kho theo từng lô
 */
@Data
@Accessors(chain = true)
public class InventoryCount {
    private UUID id;
    private String countCode;                      // Mã phiếu kiểm kê (auto-generated)
    private UUID warehouseId;                      // Kho kiểm kê
    private LocalDateTime countDate;               // Ngày kiểm kê
    private InventoryCountStatus countStatus;      // Trạng thái kiểm kê
    private String notes;                          // Ghi chú
    private UUID adjustmentTransactionId;          // Phiếu điều chỉnh được tạo (nullable)
    private UUID performedBy;                      // Người thực hiện
    private DataStatus status;                     // Trạng thái dữ liệu
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void validate() {
        if (countCode == null || countCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu kiểm kê không được để trống");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Kho không được để trống");
        }
        if (countDate == null) {
            throw new IllegalArgumentException("Ngày kiểm kê không được để trống");
        }
        if (countStatus == null) {
            throw new IllegalArgumentException("Trạng thái kiểm kê không được để trống");
        }
    }

    public void complete() {
        if (this.countStatus == InventoryCountStatus.COMPLETED) {
            throw new IllegalStateException("Phiếu kiểm kê đã hoàn thành");
        }
        if (this.countStatus == InventoryCountStatus.CANCELLED) {
            throw new IllegalStateException("Phiếu kiểm kê đã bị hủy");
        }
        this.countStatus = InventoryCountStatus.COMPLETED;
    }

    public void cancel() {
        if (this.countStatus == InventoryCountStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy phiếu kiểm kê đã hoàn thành");
        }
        if (this.countStatus == InventoryCountStatus.CANCELLED) {
            throw new IllegalStateException("Phiếu kiểm kê đã bị hủy");
        }
        this.countStatus = InventoryCountStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return this.countStatus == InventoryCountStatus.COMPLETED;
    }

    public boolean canEdit() {
        return this.countStatus == InventoryCountStatus.DRAFT || 
               this.countStatus == InventoryCountStatus.IN_PROGRESS;
    }
}
