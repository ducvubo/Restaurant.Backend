package com.restaurant.ddd.domain.enums;

/**
 * Trạng thái yêu cầu báo giá (Request For Quotation)
 */
public enum RfqStatus implements CodeEnum {
    /**
     * Nháp - chưa gửi
     */
    DRAFT(1, "Nháp"),

    /**
     * Đã gửi cho nhà cung cấp
     */
    SENT(2, "Đã gửi"),

    /**
     * Đã nhận báo giá từ NCC
     */
    RECEIVED(3, "Đã nhận báo giá"),

    /**
     * Đã chấp nhận báo giá
     */
    ACCEPTED(4, "Đã chấp nhận"),

    /**
     * Từ chối báo giá
     */
    REJECTED(5, "Từ chối"),

    /**
     * Hết hạn
     */
    EXPIRED(6, "Hết hạn"),

    /**
     * Đã hủy
     */
    CANCELLED(-1, "Đã hủy");

    private final Integer code;
    private final String message;

    RfqStatus(Integer code, String message) {
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
     * Convert code to RfqStatus enum
     */
    public static RfqStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RfqStatus status : RfqStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RfqStatus code: " + code);
    }
}
