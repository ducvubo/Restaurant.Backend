package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain Model cho Workflow
 * Định nghĩa quy trình làm việc sử dụng BPMN XML
 * 
 * Pure POJO - không có JPA annotations
 * Toàn bộ business logic nằm trong model này
 */
public class Workflow {
    private UUID id;
    private WorkflowType workflowType;
    private String description;
    private String workflowDiagram; // BPMN XML
    private List<String> listPolicy; // List of policy IDs (as JSON string in DB)
    private String version; // Version quản lý (e.g., 1.0, 1.1, 2.0)
    private DataStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private UUID createdBy;
    private UUID updatedBy;

    // ===== Constructors =====
    public Workflow() {
    }

    public Workflow(UUID id, WorkflowType workflowType, String description, 
                   String workflowDiagram, String version) {
        this.id = id;
        this.workflowType = workflowType;
        this.description = description;
        this.workflowDiagram = workflowDiagram;
        this.listPolicy = new ArrayList<>();
        this.version = version;
        this.status = DataStatus.ACTIVE;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ===== Business Methods =====

    /**
     * Kiểm tra xem workflow này có đang active không
     */
    public boolean isActive() {
        return status == DataStatus.ACTIVE;
    }

    /**
     * Kích hoạt workflow
     */
    public void activate() {
        this.status = DataStatus.ACTIVE;
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Vô hiệu hóa workflow
     */
    public void deactivate() {
        this.status = DataStatus.INACTIVE;
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Thêm policy ID vào danh sách
     */
    public void addPolicy(String policyId) {
        if (!listPolicy.contains(policyId)) {
            listPolicy.add(policyId);
        }
    }

    /**
     * Xóa policy ID khỏi danh sách
     */
    public void removePolicy(String policyId) {
        listPolicy.remove(policyId);
    }

    /**
     * Cập nhật danh sách policies
     */
    public void setPolicies(List<String> policies) {
        this.listPolicy = policies != null ? new ArrayList<>(policies) : new ArrayList<>();
    }

    /**
     * Tăng version workflow (e.g., 1.0 -> 1.1 hoặc 1.1 -> 2.0)
     */
    public void incrementVersion() {
        if (version == null || version.isEmpty()) {
            version = "1.0";
            return;
        }

        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            try {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                
                // Increment minor version
                minor++;
                
                // If minor reaches 10, increment major and reset minor
                if (minor >= 10) {
                    major++;
                    minor = 0;
                }
                
                this.version = major + "." + minor;
            } catch (NumberFormatException e) {
                // If parsing fails, reset to 1.0
                this.version = "1.0";
            }
        }
    }

    /**
     * Kiểm tra xem workflow có thể xóa được không
     * Chỉ có thể xóa nếu không ở trạng thái ACTIVE
     */
    public boolean canDelete() {
        return status != DataStatus.ACTIVE;
    }

    /**
     * Validate workflow
     */
    public void validate() {
        if (workflowType == null) {
            throw new IllegalArgumentException("Workflow type không được để trống");
        }
        if (workflowDiagram == null || workflowDiagram.trim().isEmpty()) {
            throw new IllegalArgumentException("Workflow diagram không được để trống");
        }
    }

    // ===== Getters & Setters =====
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkflowDiagram() {
        return workflowDiagram;
    }

    public void setWorkflowDiagram(String workflowDiagram) {
        this.workflowDiagram = workflowDiagram;
    }

    public List<String> getListPolicy() {
        return listPolicy;
    }

    public void setListPolicy(List<String> listPolicy) {
        this.listPolicy = listPolicy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DataStatus getStatus() {
        return status;
    }

    public void setStatus(DataStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
}
