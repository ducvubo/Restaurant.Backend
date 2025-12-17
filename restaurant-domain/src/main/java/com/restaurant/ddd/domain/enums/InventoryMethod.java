package com.restaurant.ddd.domain.enums;

/**
 * Phương pháp tính giá xuất kho
 */
public enum InventoryMethod implements CodeEnum {
    /**
     * First In First Out - Nhập trước xuất trước
     */
    FIFO(1, "FIFO - Nhập trước xuất trước"),

    /**
     * Last In First Out - Nhập sau xuất trước
     */
    LIFO(2, "LIFO - Nhập sau xuất trước");

    private final Integer code;
    private final String message;

    InventoryMethod(Integer code, String message) {
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

    public static InventoryMethod fromCode(Integer code) {
        if (code == null) return null;
        for (InventoryMethod method : InventoryMethod.values()) {
            if (method.code.equals(code)) return method;
        }
        throw new IllegalArgumentException("Invalid InventoryMethod code: " + code);
    }
}
