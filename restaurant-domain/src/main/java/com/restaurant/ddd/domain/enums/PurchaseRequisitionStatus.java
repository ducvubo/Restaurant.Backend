package com.restaurant.ddd.domain.enums;

/**
 * Trạng thái yêu cầu mua hàng (Purchase Requisition)
 */
public enum PurchaseRequisitionStatus implements CodeEnum {
    /**
     * Nháp - chưa gửi phê duyệt
     */
    DRAFT(1, "Nháp"),

    /**
     * Chờ phê duyệt
     */
    PENDING_APPROVAL(2, "Chờ phê duyệt"),

    /**
     * Đã phê duyệt
     */
    APPROVED(3, "Đã phê duyệt"),

    /**
     * Từ chối
     */
    REJECTED(4, "Từ chối"),

    /**
     * Đã chuyển thành đơn đặt hàng
     */
    CONVERTED(5, "Đã chuyển thành PO"),

    /**
     * Đã hủy
     */
    CANCELLED(-1, "Đã hủy");

    private final Integer code;
    private final String message;

    PurchaseRequisitionStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    /**
     * Convert code to PurchaseRequisitionStatus enum
     */
    public static PurchaseRequisitionStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PurchaseRequisitionStatus status : PurchaseRequisitionStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PurchaseRequisitionStatus code: " + code);
    }
}
