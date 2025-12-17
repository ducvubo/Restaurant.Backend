package com.restaurant.ddd.domain.enums;

public enum StockInType implements CodeEnum {
    EXTERNAL(1, "Nhập từ nhà cung cấp"),
    INTERNAL_TRANSFER(2, "Chuyển kho nội bộ");

    private final Integer code;
    private final String message;

    StockInType(Integer code, String message) {
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

    public static StockInType fromCode(Integer code) {
        if (code == null) return null;
        for (StockInType type : StockInType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid StockInType code: " + code);
    }
}
