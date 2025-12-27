package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity cho Workflow
 * Lưu trữ trong database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workflows", indexes = {
    @Index(name = "idx_workflow_type", columnList = "workflow_type"),
    @Index(name = "idx_status", columnList = "status")
})
@EqualsAndHashCode(callSuper = true)
public class WorkflowJpaEntity extends BaseJpaEntity {

    @Column(name = "workflow_type", nullable = false)
    private int workflowType; // Enum value

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "workflow_diagram", columnDefinition = "TEXT", nullable = false)
    private String workflowDiagram; // BPMN XML

    @Column(name = "list_policy", columnDefinition = "TEXT")
    private String listPolicy; // JSON array của policy IDs

    @Column(name = "version", nullable = false)
    private String version;

    // Constructor for creation
    public WorkflowJpaEntity(int workflowType, String description, 
                            String workflowDiagram, String version) {
        this.workflowType = workflowType;
        this.description = description;
        this.workflowDiagram = workflowDiagram;
        this.version = version;
        this.setStatus(DataStatus.ACTIVE);
    }
}
