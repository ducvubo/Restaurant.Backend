package com.restaurant.ddd.domain.enums;

/**
 * AdjustmentType - Loại điều chỉnh kho
 */
public enum AdjustmentType implements CodeEnum {
    INCREASE(1, "Điều chỉnh tăng"),
    DECREASE(2, "Điều chỉnh giảm"),
    INVENTORY_COUNT(3, "Kiểm kê kho");

    private final Integer code;
    private final String message;

    AdjustmentType(Integer code, String message) {
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

    public static AdjustmentType fromCode(Integer code) {
        if (code == null) return null;
        for (AdjustmentType type : AdjustmentType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid AdjustmentType code: " + code);
    }
}
