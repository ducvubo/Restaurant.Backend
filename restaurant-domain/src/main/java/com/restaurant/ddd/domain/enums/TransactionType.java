package com.restaurant.ddd.domain.enums;

/**
 * Loại giao dịch kho
 */
public enum TransactionType implements CodeEnum {
    /**
     * Nhập kho
     */
    STOCK_IN(1, "Nhập Kho"),

    /**
     * Xuất kho
     */
    STOCK_OUT(2, "Xuất Kho");

    private final Integer code;
    private final String message;

    TransactionType(Integer code, String message) {
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

    public static TransactionType fromCode(Integer code) {
        if (code == null) return null;
        for (TransactionType type : TransactionType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid TransactionType code: " + code);
    }
}
