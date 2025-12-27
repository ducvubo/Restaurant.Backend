package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.WorkflowMapper;
import com.restaurant.ddd.application.model.workflow.*;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.model.Workflow;
import com.restaurant.ddd.domain.respository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation của WorkflowAppService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowAppServiceImpl implements com.restaurant.ddd.application.service.WorkflowAppService {

    private final WorkflowRepository workflowRepository;
    private static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    @Override
    @Transactional
    public ResultMessage<WorkflowDTO> create(CreateWorkflowRequest request, UUID userId, boolean isForceActive) {
        log.info("WorkflowAppService: Creating workflow - type: {}", 
                request.getWorkflowType());

        try {
            // Validate request
            if (request.getWorkflowType() == null) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Workflow type không được để trống", null);
            }

            // Validate BPMN XML
            BpmnValidationResult validation = validateBpmn(request.getWorkflowDiagram());
            if (!validation.isValid()) {
                String errorMsg = "BPMN XML không hợp lệ: " + String.join(", ", validation.getErrors());
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, errorMsg, null);
            }

            // Business rule: Chỉ 1 active workflow per type
            if (request.getStatus() == DataStatus.ACTIVE) {
                Optional<Workflow> existingActive = workflowRepository.findActiveByType(
                        request.getWorkflowType());

                if (existingActive.isPresent()) {
                    if (!isForceActive) {
                        return new ResultMessage<>(ResultCode.PARAMS_ERROR,
                                "Đã có quy trình đang kích hoạt cho loại này. Vui lòng vô hiệu hóa hoặc chọn kích hoạt bắt buộc.", null);
                    } else {
                        // Disable existing active workflow
                        Workflow existing = existingActive.get();
                        existing.deactivate();
                        existing.setUpdatedBy(userId);
                        workflowRepository.save(existing);
                    }
                }
            }

            // Extract policy IDs from BPMN
            List<String> policies = extractPolicyIdsFromBpmn(request.getWorkflowDiagram());

            // Create domain model
            Workflow workflow = WorkflowMapper.toDomain(request);
            workflow.setCreatedBy(userId);
            workflow.setUpdatedBy(userId);
            workflow.setListPolicy(policies);
            
            // Get next version
            String currentMaxVersion = workflowRepository.getMaxVersion(request.getWorkflowType());
            String nextVersion = calculateNextVersion(currentMaxVersion);
            workflow.setVersion(nextVersion);

            // Save
            Workflow savedWorkflow = workflowRepository.save(workflow);

            return new ResultMessage<>(ResultCode.SUCCESS, "Tạo quy trình thành công", 
                    WorkflowMapper.toDTO(savedWorkflow));

        } catch (Exception e) {
            log.error("Error creating workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Tạo quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<WorkflowDTO> update(UUID id, UpdateWorkflowRequest request, UUID userId, boolean isForceActive) {
        log.info("WorkflowAppService: Updating workflow - id: {}", id);

        try {
            // Get existing workflow
            Optional<Workflow> existing = workflowRepository.findById(id);
            if (!existing.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình", null);
            }

            Workflow workflow = existing.get();

            // Validate BPMN XML nếu có thay đổi
            if (request.getWorkflowDiagram() != null) {
                BpmnValidationResult validation = validateBpmn(request.getWorkflowDiagram());
                if (!validation.isValid()) {
                    String errorMsg = "BPMN XML không hợp lệ: " + String.join(", ", validation.getErrors());
                    return new ResultMessage<>(ResultCode.PARAMS_ERROR, errorMsg, null);
                }
                workflow.setWorkflowDiagram(request.getWorkflowDiagram());

                // Extract policies from new BPMN
                List<String> policies = extractPolicyIdsFromBpmn(request.getWorkflowDiagram());
                workflow.setListPolicy(policies);
            }

            // Business rule: Chỉ 1 active workflow per type
            if (request.getStatus() == DataStatus.ACTIVE) {
                // Sử dụng workflowType từ workflow hiện tại (không phải từ request)
                Optional<Workflow> otherActive = workflowRepository.findActiveByType(workflow.getWorkflowType());

                if (otherActive.isPresent() && !otherActive.get().getId().equals(id)) {
                    if (!isForceActive) {
                        return new ResultMessage<>(ResultCode.PARAMS_ERROR,
                                "Đã có quy trình khác đang kích hoạt cho loại '" + workflow.getWorkflowType().message() + "'. Vui lòng vô hiệu hóa hoặc chọn ép buộc kích hoạt.", null);
                    } else {
                        // Disable other active workflow
                        Workflow other = otherActive.get();
                        other.deactivate();
                        other.setUpdatedBy(userId);
                        workflowRepository.save(other);
                        log.info("Force activated: Deactivated workflow - id: {}", other.getId());
                    }
                }
            }

            // Update fields
            if (request.getDescription() != null) {
                workflow.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                workflow.setStatus(request.getStatus());
            }
            workflow.setUpdatedBy(userId);
            workflow.setUpdatedDate(LocalDateTime.now());

            // Save
            Workflow updatedWorkflow = workflowRepository.save(workflow);

            return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật quy trình thành công", 
                    WorkflowMapper.toDTO(updatedWorkflow));

        } catch (Exception e) {
            log.error("Error updating workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Cập nhật quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<String> delete(UUID id, UUID userId) {
        log.info("WorkflowAppService: Deleting workflow - id: {}", id);

        try {
            Optional<Workflow> existing = workflowRepository.findById(id);
            if (!existing.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình", null);
            }

            Workflow workflow = existing.get();

            // Can only delete if NOT ACTIVE
            if (!workflow.canDelete()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, 
                        "Không thể xóa quy trình đang kích hoạt", null);
            }

            workflowRepository.delete(id);

            return new ResultMessage<>(ResultCode.SUCCESS, "Xóa quy trình thành công", null);

        } catch (Exception e) {
            log.error("Error deleting workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Xóa quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<WorkflowDTO> getById(UUID id) {
        log.debug("WorkflowAppService: Getting workflow by id - {}", id);

        try {
            Optional<Workflow> workflow = workflowRepository.findById(id);
            if (!workflow.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình", null);
            }

            return new ResultMessage<>(ResultCode.SUCCESS, "Lấy quy trình thành công", 
                    WorkflowMapper.toDTO(workflow.get()));

        } catch (Exception e) {
            log.error("Error getting workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Lấy quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<WorkflowListResponse> getList(WorkflowListRequest request) {
        log.debug("WorkflowAppService: Getting workflows - page: {}, size: {}, type: {}, status: {}, keyword: {}", 
                request.getPage(), request.getSize(), request.getWorkflowType(), request.getStatus(), request.getKeyword());

        try {
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getSize() != null ? request.getSize() : 10;
            
            // Use filters if provided
            Page<Workflow> workflowPage = workflowRepository.findAllWithFilters(
                    request.getWorkflowType(),
                    request.getStatus(),
                    request.getKeyword(),
                    PageRequest.of(page - 1, size));

            List<WorkflowDTO> items = workflowPage.getContent().stream()
                    .map(WorkflowMapper::toDTO)
                    .collect(Collectors.toList());

            WorkflowListResponse response = new WorkflowListResponse();
            response.setItems(items);
            response.setPage(page);
            response.setSize(size);
            response.setTotal(workflowPage.getTotalElements());
            response.setTotalPages(workflowPage.getTotalPages());

            return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách quy trình thành công", response);

        } catch (Exception e) {
            log.error("Error getting workflows: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Lấy danh sách quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<WorkflowDTO> getActiveByType(WorkflowType workflowType) {
        log.debug("WorkflowAppService: Getting active workflow - type: {}", workflowType);

        try {
            Optional<Workflow> workflow = workflowRepository.findActiveByType(workflowType);
            if (!workflow.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình đang kích hoạt", null);
            }

            return new ResultMessage<>(ResultCode.SUCCESS, "Lấy quy trình thành công", 
                    WorkflowMapper.toDTO(workflow.get()));

        } catch (Exception e) {
            log.error("Error getting active workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Lấy quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    public BpmnValidationResult validateBpmn(String bpmnXml) {
        log.debug("WorkflowAppService: Validating BPMN XML");

        BpmnValidationResult result = new BpmnValidationResult(true);

        try {
            if (bpmnXml == null || bpmnXml.trim().isEmpty()) {
                result.addError("BPMN XML không được để trống");
                return result;
            }

            // Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(bpmnXml)));

            // Get process element
            NodeList processList = doc.getElementsByTagNameNS(BPMN_NAMESPACE, "process");
            if (processList.getLength() == 0) {
                result.addError("Không tìm thấy thẻ <bpmn:process> trong XML");
                return result;
            }

            Element process = (Element) processList.item(0);

            // Check Start Event
            NodeList startEvents = process.getElementsByTagNameNS(BPMN_NAMESPACE, "startEvent");
            if (startEvents.getLength() != 1) {
                result.addError("Quy trình phải có CHÍNH XÁC 1 Start Event (hiện có " + startEvents.getLength() + ")");
            }

            // Check End Event
            NodeList endEvents = process.getElementsByTagNameNS(BPMN_NAMESPACE, "endEvent");
            if (endEvents.getLength() != 1) {
                result.addError("Quy trình phải có CHÍNH XÁC 1 End Event (hiện có " + endEvents.getLength() + ")");
            }

            // Check Tasks
            NodeList tasks = process.getElementsByTagNameNS(BPMN_NAMESPACE, "task");
            if (tasks.getLength() < 1) {
                result.addError("Quy trình phải có ít nhất 1 Task");
            }

            // Check Sequence Flows (connectivity)
            NodeList sequenceFlows = process.getElementsByTagNameNS(BPMN_NAMESPACE, "sequenceFlow");
            if (sequenceFlows.getLength() < 1) {
                result.addError("Quy trình phải có ít nhất 1 Sequence Flow kết nối các phần tử");
            }

        } catch (Exception e) {
            log.error("BPMN validation error: {}", e.getMessage(), e);
            result.addError("Lỗi parsing XML: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<String> extractPolicyIdsFromBpmn(String bpmnXml) {
        log.debug("WorkflowAppService: Extracting policy IDs from BPMN");

        Set<String> policyIds = new HashSet<>();

        try {
            if (bpmnXml == null || bpmnXml.trim().isEmpty()) {
                return new ArrayList<>();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(bpmnXml)));

            NodeList processList = doc.getElementsByTagNameNS(BPMN_NAMESPACE, "process");
            if (processList.getLength() == 0) {
                return new ArrayList<>();
            }

            Element process = (Element) processList.item(0);

            // Extract policy IDs from task elements (policyId attribute contains JSON array)
            NodeList tasks = process.getElementsByTagNameNS(BPMN_NAMESPACE, "task");
            for (int i = 0; i < tasks.getLength(); i++) {
                Element task = (Element) tasks.item(i);
                String policyIdAttribute = task.getAttribute("policyId");

                if (policyIdAttribute != null && !policyIdAttribute.isEmpty()) {
                    // policyId is stored as JSON array, e.g., ["uuid1", "uuid2"]
                    try {
                        // Remove brackets and quotes, then split
                        String cleanedString = policyIdAttribute
                                .replace("[", "")
                                .replace("]", "")
                                .replace("\"", "")
                                .replace("'", "");
                        
                        if (!cleanedString.isEmpty()) {
                            String[] ids = cleanedString.split(",");
                            for (String id : ids) {
                                String trimmedId = id.trim();
                                if (!trimmedId.isEmpty()) {
                                    policyIds.add(trimmedId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse policyId attribute: {}", policyIdAttribute);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error extracting policy IDs: {}", e.getMessage(), e);
        }

        return new ArrayList<>(policyIds);
    }

    @Override
    @Transactional
    public ResultMessage<String> activate(UUID id, UUID userId, boolean isForceActive) {
        log.info("WorkflowAppService: Activating workflow - id: {}, isForceActive: {}", id, isForceActive);

        try {
            Optional<Workflow> existing = workflowRepository.findById(id);
            if (!existing.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình", null);
            }

            Workflow workflow = existing.get();
            
            // Business rule: Chỉ 1 active workflow per type
            Optional<Workflow> currentActive = workflowRepository.findActiveByType(workflow.getWorkflowType());
            if (currentActive.isPresent() && !currentActive.get().getId().equals(id)) {
                if (!isForceActive) {
                    // Không ép buộc: trả về lỗi
                    return new ResultMessage<>(ResultCode.PARAMS_ERROR,
                            "Đã có quy trình khác đang kích hoạt cho loại '" + workflow.getWorkflowType().message() + "'. Vui lòng chọn ép buộc kích hoạt.", null);
                } else {
                    // Ép buộc: deactivate workflow cũ
                    Workflow activeWorkflow = currentActive.get();
                    activeWorkflow.deactivate();
                    activeWorkflow.setUpdatedBy(userId);
                    workflowRepository.save(activeWorkflow);
                    log.info("Force activated: Deactivated existing workflow - id: {}", activeWorkflow.getId());
                }
            }
            
            workflow.activate();
            workflow.setUpdatedBy(userId);

            workflowRepository.save(workflow);

            return new ResultMessage<>(ResultCode.SUCCESS, "Kích hoạt quy trình thành công", null);

        } catch (Exception e) {
            log.error("Error activating workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Kích hoạt quy trình thất bại: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<String> deactivate(UUID id, UUID userId) {
        log.info("WorkflowAppService: Deactivating workflow - id: {}", id);

        try {
            Optional<Workflow> existing = workflowRepository.findById(id);
            if (!existing.isPresent()) {
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Không tìm thấy quy trình", null);
            }

            Workflow workflow = existing.get();
            workflow.deactivate();
            workflow.setUpdatedBy(userId);

            workflowRepository.save(workflow);

            return new ResultMessage<>(ResultCode.SUCCESS, "Vô hiệu hóa quy trình thành công", null);

        } catch (Exception e) {
            log.error("Error deactivating workflow: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Vô hiệu hóa quy trình thất bại: " + e.getMessage(), null);
        }
    }

    /**
     * Calculate next version based on current max version
     * e.g., 1.0 -> 1.1 -> 1.2 -> ... -> 2.0
     */
    private String calculateNextVersion(String currentMaxVersion) {
        if (currentMaxVersion == null || currentMaxVersion.equals("0.0")) {
            return "1.0";
        }

        String[] parts = currentMaxVersion.split("\\.");
        if (parts.length == 2) {
            try {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);

                minor++;
                if (minor >= 10) {
                    major++;
                    minor = 0;
                }

                return major + "." + minor;
            } catch (NumberFormatException e) {
                return "1.0";
            }
        }

        return "1.0";
    }
}
