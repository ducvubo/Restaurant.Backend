package com.restaurant.ddd.domain.enums;

/**
 * Trạng thái dữ liệu
 */
public enum DataStatus implements CodeEnum {
    /**
     * Đang hoạt động
     */
    ACTIVE(1, "Đang hoạt động"),

    /**
     * Không hoạt động
     */
    INACTIVE(0, "Không hoạt động"),

    /**
     * Đã xóa
     */
    DELETED(-1, "Đã xóa");

    private final Integer code;
    private final String message;

    DataStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    /**
     * Convert code to DataStatus enum
     */
    public static DataStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DataStatus status : DataStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid DataStatus code: " + code);
    }
}

