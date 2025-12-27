package com.restaurant.ddd.domain.enums;

/**
 * Enum định nghĩa các loại hành động trong Workflow
 */
public enum WorkflowActionType  implements CodeEnum {
    NEXT_STEP(1, "Chuyển bước"),
    CONDITION(2, "Điều kiện"),
    END(3, "Kết thúc");

    private final Integer code;
    private final String message;

    WorkflowActionType(Integer code, String message) {
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

    public static WorkflowActionType fromCode(Integer code) {
        if (code == null) return null;
        for (WorkflowActionType type : WorkflowActionType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid WarehouseType code: " + code);
    }
}
