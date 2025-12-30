package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Thông tin trạng thái workflow hiện tại
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStateDTO {
    
    /**
     * ID bước hiện tại
     */
    private String currentStepId;
    
    /**
     * Tên bước hiện tại
     */
    private String currentStepName;
    
    /**
     * Loại bước (StartEvent, Task, EndEvent, Gateway)
     */
    private String currentStepType;
    
    /**
     * Có phải là bước kết thúc không
     */
    private boolean isEndStep;
    
    /**
     * Workflow đã hoàn thành chưa
     */
    private boolean isComplete;
    
    /**
     * Danh sách actions có thể thực hiện ở bước hiện tại
     */
    private List<WorkflowActionOption> availableActions;
    
    /**
     * Option cho một action
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowActionOption {
        private String actionKey;
        private String actionName;
        private String targetStepId;
    }
}
