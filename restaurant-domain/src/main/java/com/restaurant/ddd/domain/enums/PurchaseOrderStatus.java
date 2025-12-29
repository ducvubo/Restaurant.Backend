package com.restaurant.ddd.domain.enums;

/**
 * Trạng thái đơn đặt hàng (Purchase Order)
 */
public enum PurchaseOrderStatus implements CodeEnum {
    /**
     * Nháp - chưa xác nhận
     */
    DRAFT(1, "Nháp"),

    /**
     * Đã xác nhận với nhà cung cấp
     */
    CONFIRMED(2, "Đã xác nhận"),

    /**
     * Đã nhận một phần hàng
     */
    PARTIALLY_RECEIVED(3, "Nhận một phần"),

    /**
     * Đã nhận đủ hàng
     */
    COMPLETED(4, "Hoàn thành"),

    /**
     * Đã hủy
     */
    CANCELLED(-1, "Đã hủy");

    private final Integer code;
    private final String message;

    PurchaseOrderStatus(Integer code, String message) {
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
     * Convert code to PurchaseOrderStatus enum
     */
    public static PurchaseOrderStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PurchaseOrderStatus status : PurchaseOrderStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PurchaseOrderStatus code: " + code);
    }
}
