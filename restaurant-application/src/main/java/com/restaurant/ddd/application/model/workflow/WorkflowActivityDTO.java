package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO cho Activity log
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowActivityDTO {
    
    private UUID id;
    private String stepId;
    private String stepName;
    private String content;
    private LocalDateTime actionDate;
    private UUID userId;
    private String userName; // Tên người thực hiện (nếu có)
}
