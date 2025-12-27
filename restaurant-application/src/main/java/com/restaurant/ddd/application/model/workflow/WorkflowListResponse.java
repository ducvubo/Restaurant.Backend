package com.restaurant.ddd.application.model.workflow;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Pagination Response cho Workflow list
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowListResponse extends PageResponse<WorkflowDTO> {
    // Can add specific fields if needed
}
