package com.restaurant.ddd.domain.enums;

public enum StockOutType implements CodeEnum {
    INTERNAL_TRANSFER(1, "Chuyển kho nội bộ"),
    RETAIL_SALE(2, "Bán lẻ"),
    DISPOSAL(3, "Tiêu hủy");

    private final Integer code;
    private final String message;

    StockOutType(Integer code, String message) {
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

    public static StockOutType fromCode(Integer code) {
        if (code == null) return null;
        for (StockOutType type : StockOutType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid StockOutType code: " + code);
    }
}
