package com.restaurant.ddd.domain.enums;

import lombok.Getter;

public enum CustomerType implements CodeEnum  {
    INDIVIDUAL(1, "Cá nhân"),
    COMPANY(2, "Doanh nghiệp");

    private final Integer code;
    private final String message;

    CustomerType(Integer code, String message) {
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

    public static CustomerType fromCode(Integer code) {
        if (code == null) return null;
        for (CustomerType type : CustomerType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid WarehouseType code: " + code);
    }
}
