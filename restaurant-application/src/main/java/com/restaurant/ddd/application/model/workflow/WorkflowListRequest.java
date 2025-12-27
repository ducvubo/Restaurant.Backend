package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho danh sách workflow
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowListRequest {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private Integer workflowType;
    private Integer status;
    private String keyword;
}

