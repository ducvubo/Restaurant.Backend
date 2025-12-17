package com.restaurant.ddd.domain.enums;

/**
 * Loại kho
 */
public enum WarehouseType implements CodeEnum {
    /**
     * Kho trung tâm - phục vụ toàn bộ hệ thống
     */
    CENTRAL(1, "Kho Trung Tâm"),

    /**
     * Kho chi nhánh - phục vụ chi nhánh cụ thể
     */
    BRANCH(2, "Kho Chi Nhánh");

    private final Integer code;
    private final String message;

    WarehouseType(Integer code, String message) {
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

    public static WarehouseType fromCode(Integer code) {
        if (code == null) return null;
        for (WarehouseType type : WarehouseType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid WarehouseType code: " + code);
    }
}
