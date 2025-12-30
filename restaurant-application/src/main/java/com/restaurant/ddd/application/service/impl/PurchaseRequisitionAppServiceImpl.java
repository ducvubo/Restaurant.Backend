package com.restaurant.ddd.application.service.impl;

//import com.restaurant.ddd.application.mapper.PurchaseRequisitionMapperNew;
import com.restaurant.ddd.application.mapper.PurchaseRequisitionMapperNew;
import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.model.workflow.*;
import com.restaurant.ddd.application.service.PurchaseRequisitionAppService;
import com.restaurant.ddd.application.util.WorkflowUtils;
import com.restaurant.ddd.domain.enums.PurchasePriority;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;
import com.restaurant.ddd.domain.model.Warehouse;
import com.restaurant.ddd.domain.model.Workflow;
import com.restaurant.ddd.domain.respository.PurchaseRequisitionRepository;
import com.restaurant.ddd.domain.respository.WarehouseRepository;
import com.restaurant.ddd.domain.respository.WorkflowRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.WorkflowActivityJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.UserJpaMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.WorkflowActivityJpaRepository;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PurchaseRequisitionAppService
 */
@Service
@Slf4j
public class PurchaseRequisitionAppServiceImpl implements PurchaseRequisitionAppService {

    @Autowired
    private PurchaseRequisitionRepository requisitionRepository;
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private WorkflowActivityJpaRepository activityRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private UserJpaMapper userJpaMapper;
    
    @Autowired
    private com.restaurant.ddd.infrastructure.persistence.mapper.PolicyJpaRepository policyJpaRepository;

    @Override
    @Transactional
    public PurchaseRequisitionDTO create(PurchaseRequisitionRequest request) {
        log.info("Creating new purchase requisition");
        
        // Kiểm tra phải có workflow active cho PURCHASE_REQUEST
        Optional<Workflow> activeWorkflow = workflowRepository.findActiveByType(WorkflowType.PURCHASE_REQUEST);
        if (activeWorkflow.isEmpty()) {
            throw new RuntimeException("Chưa có quy trình phê duyệt yêu cầu mua hàng được kích hoạt. Vui lòng liên hệ quản trị viên.");
        }
        
        Workflow workflow = activeWorkflow.get();
        
        // Lấy StartEvent từ BPMN diagram
        WorkflowStateDTO startInfo = WorkflowUtils.getStartEventInfo(workflow.getWorkflowDiagram());
        if (startInfo == null) {
            throw new RuntimeException("Quy trình không hợp lệ: không tìm thấy bước bắt đầu.");
        }
        
        // Generate code
        String code = requisitionRepository.generateNextCode();
        
        // Create domain model
        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setId(UUID.randomUUID());
        pr.setRequisitionCode(code);
        pr.setWarehouseId(request.getWarehouseId());
        pr.setRequestedBy(SecurityUtils.getCurrentUserId());
        pr.setRequestDate(LocalDateTime.now());
        pr.setRequiredDate(request.getRequiredDate());
        pr.setPriority(PurchasePriority.fromCode(request.getPriority()));
        pr.setNotes(request.getNotes());
        pr.setStatus(PurchaseRequisitionStatus.DRAFT);
        pr.setCreatedBy(SecurityUtils.getCurrentUserId());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Gán workflow cho phiếu
        pr.setWorkflowId(workflow.getId());
        pr.setWorkflowStep(startInfo.getCurrentStepId());
        
        // Map items
        if (request.getItems() != null) {
            List<PurchaseRequisitionItem> items = PurchaseRequisitionMapperNew.toItemDomainList(request.getItems());
            pr.setItems(items);
        }
        
        // Validate
        pr.validate();
        
        // Save
        PurchaseRequisition saved = requisitionRepository.save(pr);
        
        // Lưu activity log cho việc tạo phiếu
        saveActivity(saved.getId(), saved.getWorkflowId(), startInfo.getCurrentStepId(), startInfo.getCurrentStepName(), 
                "Tạo yêu cầu mua hàng mới");
        
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    public PurchaseRequisitionDTO getById(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        PurchaseRequisitionDTO dto = PurchaseRequisitionMapperNew.toDTO(pr);
        // Enrich the DTO with names
        enrichDTOs(java.util.Collections.singletonList(dto));
        return dto;
    }

    @Override
    public PageResponse<PurchaseRequisitionDTO> getList(PurchaseListRequest request) {
        // Build Pageable
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDir()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        int page = request.getPage() != null ? Math.max(0, request.getPage() - 1) : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Query
        Page<PurchaseRequisition> result = requisitionRepository.findAll(
                request.getKeyword(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        );
        
        // Map to DTOs
        List<PurchaseRequisitionDTO> dtos = result.getContent().stream()
                .map(PurchaseRequisitionMapperNew::toDTO)
                .collect(Collectors.toList());
        
        // Enrich warehouse names and user names
        enrichDTOs(dtos);
        
        return PageResponse.of(dtos, request.getPage(), size, result.getTotalElements());
    }
    
    /**
     * Enrich DTOs with warehouse names and user names
     */
    private void enrichDTOs(List<PurchaseRequisitionDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;
        
        // Collect all warehouse IDs and user IDs
        List<UUID> warehouseIds = dtos.stream()
                .map(PurchaseRequisitionDTO::getWarehouseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        List<UUID> userIds = dtos.stream()
                .map(PurchaseRequisitionDTO::getRequestedBy)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        // Load warehouses
        Map<UUID, String> warehouseNames = warehouseIds.stream()
                .map(warehouseRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Warehouse::getId, Warehouse::getName));
        
        // Load users
        Map<UUID, String> userNames = userIds.stream()
                .map(userJpaMapper::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(UserManagementJpaEntity::getId, UserManagementJpaEntity::getFullName));
        
        // Enrich DTOs
        for (PurchaseRequisitionDTO dto : dtos) {
            if (dto.getWarehouseId() != null) {
                dto.setWarehouseName(warehouseNames.get(dto.getWarehouseId()));
            }
            if (dto.getRequestedBy() != null) {
                dto.setRequestedByName(userNames.get(dto.getRequestedBy()));
            }
            // Enrich workflow step name và required policies
            if (dto.getWorkflowId() != null && dto.getWorkflowStep() != null) {
                workflowRepository.findById(dto.getWorkflowId())
                        .ifPresent(wf -> {
                            String stepName = WorkflowUtils.getStepName(wf.getWorkflowDiagram(), dto.getWorkflowStep());
                            dto.setWorkflowStepName(stepName);
                            
                            // Xác định quyền thực hiện dựa trên bước hiện tại
                            String stepId = dto.getWorkflowStep();
                            if ("StartEvent_Begin".equals(stepId)) {
                                // Bước bắt đầu: chỉ người tạo phiếu
                                dto.setRequiredPolicies("Người tạo phiếu");
                            } else if ("EndEvent_Completed".equals(stepId)) {
                                // Đã hoàn thành: bỏ trống
                                dto.setRequiredPolicies(null);
                            } else {
                                // Các bước khác: lấy policyIds từ BPMN
                                List<String> policyIds = WorkflowUtils.getStepPolicyIds(wf.getWorkflowDiagram(), stepId);
                                if (policyIds.isEmpty()) {
                                    dto.setRequiredPolicies("Tất cả");
                                } else {
                                    // Convert policyId thành tên policy
                                    List<String> policyNames = policyIds.stream()
                                            .map(pid -> {
                                                try {
                                                    return policyJpaRepository.findById(UUID.fromString(pid))
                                                            .map(p -> p.getName())
                                                            .orElse(null);
                                                } catch (Exception e) {
                                                    return null;
                                                }
                                            })
                                            .filter(name -> name != null)
                                            .collect(Collectors.toList());
                                    dto.setRequiredPolicies(policyNames.isEmpty() ? "Tất cả" : String.join(", ", policyNames));
                                }
                            }
                        });
            }
        }
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO update(UUID id, PurchaseRequisitionRequest request) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        // Check if can edit
        if (!pr.canEdit()) {
            throw new RuntimeException("Không thể sửa yêu cầu mua hàng ở trạng thái: " + pr.getStatus().message());
        }
        
        // Update fields
        pr.setWarehouseId(request.getWarehouseId());
        pr.setRequiredDate(request.getRequiredDate());
        pr.setPriority(PurchasePriority.fromCode(request.getPriority()));
        pr.setNotes(request.getNotes());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Update items
        if (request.getItems() != null) {
            List<PurchaseRequisitionItem> items = PurchaseRequisitionMapperNew.toItemDomainList(request.getItems());
            pr.setItems(items);
        }
        
        // Validate
        pr.validate();
        
        // Save
        PurchaseRequisition saved = requisitionRepository.save(pr);
        
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO submit(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.submit();
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO approve(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.approve(SecurityUtils.getCurrentUserId());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO reject(UUID id, String reason) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.reject(SecurityUtils.getCurrentUserId(), reason);
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO cancel(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.cancel();
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        // Chỉ cho xóa khi quy trình chưa kết thúc (EndEvent_Completed)
        String endEventId = WorkflowAppServiceImpl.WORKFLOW_END_EVENT_ID;
        if (endEventId.equals(pr.getWorkflowStep())) {
            throw new RuntimeException("Không thể xóa yêu cầu đã hoàn thành quy trình");
        }
        
        // Xóa các activity liên quan trước
        activityRepository.deleteByReferenceId(id);
        
        requisitionRepository.deleteById(id);
        log.info("Deleted purchase requisition: {}", id);
    }
    
    // ===== Workflow Methods Implementation =====
    
    @Override
    public WorkflowStateDTO getWorkflowState(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        // Nếu chưa có workflowId, lấy workflow active cho PURCHASE_REQUEST
        if (pr.getWorkflowId() == null) {
            Optional<Workflow> activeWorkflow = workflowRepository.findActiveByType(WorkflowType.PURCHASE_REQUEST);
            if (activeWorkflow.isEmpty()) {
                return WorkflowStateDTO.builder()
                        .currentStepId(null)
                        .currentStepName("Chưa có quy trình")
                        .currentStepType("None")
                        .isComplete(false)
                        .build();
            }
            return WorkflowUtils.getStartEventInfo(activeWorkflow.get().getWorkflowDiagram());
        }
        
        // Lấy workflow diagram
        Workflow workflow = workflowRepository.findById(pr.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy workflow với id: " + pr.getWorkflowId()));
        
        return WorkflowUtils.getNextStepInfo(workflow.getWorkflowDiagram(), pr.getWorkflowStep());
    }
    
    @Override
    @Transactional
    public PurchaseRequisitionDTO performWorkflowAction(UUID id, WorkflowActionRequest request) {
        log.info("Performing workflow action {} on requisition {}", request.getActionKey(), id);
        
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        // Lấy workflow active nếu chưa có
        if (pr.getWorkflowId() == null) {
            Optional<Workflow> activeWorkflow = workflowRepository.findActiveByType(WorkflowType.PURCHASE_REQUEST);
            if (activeWorkflow.isEmpty()) {
                throw new RuntimeException("Chưa có quy trình PURCHASE_REQUEST được kích hoạt");
            }
            pr.setWorkflowId(activeWorkflow.get().getId());
            
            // Set initial step (StartEvent)
            WorkflowStateDTO startInfo = WorkflowUtils.getStartEventInfo(activeWorkflow.get().getWorkflowDiagram());
            if (startInfo != null) {
                pr.setWorkflowStep(startInfo.getCurrentStepId());
            }
        }
        
        // Lấy workflow diagram
        Workflow workflow = workflowRepository.findById(pr.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy workflow"));
        
        // Lấy thông tin bước hiện tại
        WorkflowStateDTO currentState = WorkflowUtils.getNextStepInfo(
                workflow.getWorkflowDiagram(), pr.getWorkflowStep());
        
        if (currentState == null || currentState.getAvailableActions() == null) {
            throw new RuntimeException("Không thể xác định bước tiếp theo");
        }
        
        // Tìm action được chọn
        WorkflowStateDTO.WorkflowActionOption selectedAction = currentState.getAvailableActions().stream()
                .filter(a -> a.getActionKey().equals(request.getActionKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Action không hợp lệ: " + request.getActionKey()));
        
        // Check quyền: Bước bắt đầu chỉ người tạo phiếu mới được thực hiện
        String currentStepId = pr.getWorkflowStep();
        if ("StartEvent_Begin".equals(currentStepId)) {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            if (!pr.getCreatedBy().equals(currentUserId)) {
                throw new RuntimeException("Chỉ người tạo phiếu mới được thực hiện bước bắt đầu");
            }
        }
        
        // Lưu activity log
        saveActivity(pr.getId(), pr.getWorkflowId(), pr.getWorkflowStep(), currentState.getCurrentStepName(),
                request.getComment() != null ? request.getComment() : "Thực hiện: " + selectedAction.getActionName());
        
        // Chuyển sang bước mới
        String oldStep = pr.getWorkflowStep();
        pr.setWorkflowStep(selectedAction.getTargetStepId());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Kiểm tra nếu bước mới là EndEvent thì cập nhật status
        WorkflowStateDTO newState = WorkflowUtils.getNextStepInfo(
                workflow.getWorkflowDiagram(), selectedAction.getTargetStepId());
        if (newState != null && newState.isEndStep()) {
            pr.setStatus(PurchaseRequisitionStatus.APPROVED);
            pr.setApprovedBy(SecurityUtils.getCurrentUserId());
            pr.setApprovedDate(LocalDateTime.now());
        }
        
        // Lưu
        PurchaseRequisition saved = requisitionRepository.save(pr);
        log.info("Workflow action completed. Moved from {} to {}", oldStep, selectedAction.getTargetStepId());
        
        return PurchaseRequisitionMapperNew.toDTO(saved);
    }
    
    @Override
    public List<WorkflowActivityDTO> getHistory(UUID id) {
        // Kiểm tra requisition tồn tại
        requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        List<WorkflowActivityJpaEntity> activities = activityRepository
                .findByReferenceIdAndWorkflowTypeOrderByActionDateAsc(id, WorkflowType.PURCHASE_REQUEST.code());
        
        // Collect user IDs and batch load
        List<UUID> userIds = activities.stream()
                .map(WorkflowActivityJpaEntity::getUserId)
                .filter(uid -> uid != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<UUID, String> userNames = userIds.stream()
                .map(userJpaMapper::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(UserManagementJpaEntity::getId, UserManagementJpaEntity::getFullName));
        
        return activities.stream()
                .map(a -> WorkflowActivityDTO.builder()
                        .id(a.getId())
                        .stepId(a.getStepId())
                        .stepName(a.getStepName())
                        .content(a.getContent())
                        .actionDate(a.getActionDate())
                        .userId(a.getUserId())
                        .userName(userNames.get(a.getUserId()))
                        .build())
                .collect(Collectors.toList());
    }
    
    private void saveActivity(UUID referenceId, UUID workflowId, String stepId, String stepName, String content) {
        WorkflowActivityJpaEntity activity = new WorkflowActivityJpaEntity();
        activity.setId(UUID.randomUUID());
        activity.setReferenceId(referenceId);
        activity.setWorkflowType(WorkflowType.PURCHASE_REQUEST.code());
        activity.setWorkflowId(workflowId);
        activity.setStepId(stepId);
        activity.setStepName(stepName);
        activity.setContent(content);
        activity.setActionDate(LocalDateTime.now());
        activity.setUserId(SecurityUtils.getCurrentUserId());
        activity.setCreatedBy(SecurityUtils.getCurrentUserId());
        activity.setCreatedDate(LocalDateTime.now());
        
        activityRepository.save(activity);
    }
}
