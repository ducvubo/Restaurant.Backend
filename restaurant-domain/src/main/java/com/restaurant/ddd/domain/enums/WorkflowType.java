package com.restaurant.ddd.domain.enums;

/**
 * Enum định nghĩa các loại Workflow trong hệ thống nhà hàng
 */
public enum WorkflowType  implements CodeEnum{
    // Procurement (Mua sắm)
    PURCHASE_REQUEST(1, "Đề nghị mua sắm"),
    STOCK_IN_APPROVAL(2, "Phê duyệt nhập kho"),
    
    // Stock Management (Quản lý kho)
    STOCK_OUT_REQUEST(3, "Đề nghị xuất kho"),
    STOCK_OUT_APPROVAL(4, "Phê duyệt xuất kho"),
    ADJUSTMENT_REQUEST(5, "Đề nghị điều chỉnh kho"),
    ADJUSTMENT_APPROVAL(6, "Phê duyệt điều chỉnh"),
    
    // Inventory (Kiểm kê)
    INVENTORY_COUNT_REQUEST(7, "Đề nghị kiểm kê"),
    INVENTORY_COUNT_APPROVAL(8, "Phê duyệt kiểm kê"),
    
    // Other (Khác)
    MATERIAL_CREATION(9, "Tạo nguyên vật liệu mới"),
    USER_PROMOTION(10, "Đề nghị thăng chức nhân viên");

    private final Integer code;
    private final String message;

    WorkflowType(Integer code, String message) {
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

    public static WorkflowType fromCode(Integer code) {
        if (code == null) return null;
        for (WorkflowType type : WorkflowType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid WarehouseType code: " + code);
    }
}
