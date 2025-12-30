package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request để thực hiện action trong workflow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowActionRequest {
    
    /**
     * ID của action/task cần thực hiện (từ BPMN)
     */
    private String actionKey;
    
    /**
     * Ghi chú khi thực hiện action
     */
    private String comment;
}
