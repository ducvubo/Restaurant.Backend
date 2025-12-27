package com.restaurant.ddd.domain.enums;

/**
 * Enum định nghĩa các loại bước trong BPMN Workflow
 */
public enum WorkflowStepType implements CodeEnum {
    START_EVENT(1, "Bắt đầu"),
    TASK(2, "Bước"),
    GATEWAY(3, "Điều kiện"),
    END_EVENT(4, "Kết thúc");

    private final Integer code;
    private final String message;

    WorkflowStepType(Integer code, String message) {
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

    public static WorkflowStepType fromCode(Integer code) {
        if (code == null) return null;
        for (WorkflowStepType type : WorkflowStepType.values()) {
            if (type.code.equals(code)) return type;
        }
        throw new IllegalArgumentException("Invalid WorkflowStepType code: " + code);
    }
}
