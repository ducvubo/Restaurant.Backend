package com.restaurant.ddd.domain.enums;

/**
 * Độ ưu tiên của yêu cầu mua hàng
 */
public enum PurchasePriority implements CodeEnum {
    /**
     * Thấp
     */
    LOW(1, "Thấp"),

    /**
     * Bình thường
     */
    NORMAL(2, "Bình thường"),

    /**
     * Cao
     */
    HIGH(3, "Cao"),

    /**
     * Khẩn cấp
     */
    URGENT(4, "Khẩn cấp");

    private final Integer code;
    private final String message;

    PurchasePriority(Integer code, String message) {
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
     * Convert code to PurchasePriority enum
     */
    public static PurchasePriority fromCode(Integer code) {
        if (code == null) {
            return NORMAL; // Default priority
        }
        for (PurchasePriority priority : PurchasePriority.values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        return NORMAL;
    }
}
