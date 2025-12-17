package com.restaurant.ddd.domain.enums;

/**
 * Trả về mã trạng thái
 * Chữ số đầu tiên: 1: Sản phẩm; 2: Người dùng; 3: Giao dịch,
 * 4: Khuyến mãi, 5: Cửa hàng, 6: Trang web, 7: Cài đặt, 8: Khác
 */
public enum ResultCode {

    /**
     * Mã trạng thái thành công
     */
    SUCCESS(200, "Thành công"),

    /**
     * Tham số bất thường
     */
    PARAMS_ERROR(4002, "Tham số bất thường"),

    /**
     * Mã lỗi trả về
     */
    ERROR(400, "Máy chủ bận, vui lòng thử lại sau"),

    /**
     * Người dùng
     */
    USER_NOT_FOUND(20002, "Người dùng không tồn tại hoặc tài khoản đã bị vô hiệu hóa"),
    USER_SESSION_EXPIRED(20004, "Phiên đăng nhập của người dùng đã hết hạn, vui lòng đăng nhập lại"),
    USER_PERMISSION_ERROR(20005, "Quyền hạn không đủ"),

    /**
     * Sản phẩm
     */
    PRODUCT_NOT_FOUND(11001, "Sản phẩm không tồn tại"),
    PRODUCT_ERROR(11002, "Sản phẩm lỗi, vui lòng thử lại sau"),

    /**
     * Ngoại lệ hệ thống
     */
    RATE_LIMIT_ERROR(1003, "Truy cập quá thường xuyên, vui lòng thử lại sau"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

