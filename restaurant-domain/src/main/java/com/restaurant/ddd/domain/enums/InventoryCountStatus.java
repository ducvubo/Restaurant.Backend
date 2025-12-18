package com.restaurant.ddd.domain.enums;

/**
 * InventoryCountStatus - Trạng thái phiếu kiểm kê
 */
public enum InventoryCountStatus implements CodeEnum {
    DRAFT(1, "Nháp"),
    IN_PROGRESS(2, "Đang kiểm kê"),
    COMPLETED(3, "Hoàn thành"),
    CANCELLED(4, "Đã hủy");

    private final Integer code;
    private final String message;

    InventoryCountStatus(Integer code, String message) {
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

    public static InventoryCountStatus fromCode(Integer code) {
        if (code == null) return null;
        for (InventoryCountStatus status : InventoryCountStatus.values()) {
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Invalid InventoryCountStatus code: " + code);
    }
}
