package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.WorkflowMapper;
import com.restaurant.ddd.application.model.workflow.*;
import com.restaurant.ddd.application.service.WorkflowAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.WorkflowType;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.model.Workflow;
import com.restaurant.ddd.domain.respository.WorkflowRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.WorkflowActivityJpaRepository;
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
public class WorkflowAppServiceImpl implements WorkflowAppService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowActivityJpaRepository activityRepository;
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

            // Chuẩn hóa endEvent ID thành key cố định
            String normalizedBpmn = normalizeEndEventId(request.getWorkflowDiagram());
            request.setWorkflowDiagram(normalizedBpmn);

            // Extract policy IDs from BPMN
            List<String> policies = extractPolicyIdsFromBpmn(normalizedBpmn);

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
            
            // Kiểm tra workflow đã được sử dụng chưa
            boolean isUsed = isWorkflowUsed(id);
            
            if (isUsed && request.getWorkflowDiagram() != null) {
                // Workflow đã được sử dụng và có thay đổi diagram -> Tạo version mới
                log.info("Workflow {} đã được sử dụng, tạo version mới thay vì cập nhật", id);
                return createNewVersion(workflow, request, userId, isForceActive);
            }

            // Validate BPMN XML nếu có thay đổi
            if (request.getWorkflowDiagram() != null) {
                BpmnValidationResult validation = validateBpmn(request.getWorkflowDiagram());
                if (!validation.isValid()) {
                    String errorMsg = "BPMN XML không hợp lệ: " + String.join(", ", validation.getErrors());
                    return new ResultMessage<>(ResultCode.PARAMS_ERROR, errorMsg, null);
                }
                
                // Chuẩn hóa endEvent ID thành key cố định
                String normalizedBpmn = normalizeEndEventId(request.getWorkflowDiagram());
                workflow.setWorkflowDiagram(normalizedBpmn);

                // Extract policies from normalized BPMN
                List<String> policies = extractPolicyIdsFromBpmn(normalizedBpmn);
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
    
    /**
     * Kiểm tra workflow đã được sử dụng chưa (có activity nào tham chiếu)
     */
    public boolean isWorkflowUsed(UUID workflowId) {
        return activityRepository.existsByWorkflowId(workflowId);
    }
    
    /**
     * Tạo version mới của workflow khi workflow cũ đã được sử dụng
     */
    private ResultMessage<WorkflowDTO> createNewVersion(Workflow oldWorkflow, UpdateWorkflowRequest request, UUID userId, boolean isForceActive) {
        try {
            // Validate BPMN
            BpmnValidationResult validation = validateBpmn(request.getWorkflowDiagram());
            if (!validation.isValid()) {
                String errorMsg = "BPMN XML không hợp lệ: " + String.join(", ", validation.getErrors());
                return new ResultMessage<>(ResultCode.PARAMS_ERROR, errorMsg, null);
            }
            
            // Chuẩn hóa BPMN
            String normalizedBpmn = normalizeEndEventId(request.getWorkflowDiagram());
            List<String> policies = extractPolicyIdsFromBpmn(normalizedBpmn);
            
            // Tính version mới
            String newVersion = incrementVersion(oldWorkflow.getVersion());
            
            // Vô hiệu hóa workflow cũ
            oldWorkflow.deactivate();
            oldWorkflow.setUpdatedBy(userId);
            oldWorkflow.setUpdatedDate(LocalDateTime.now());
            workflowRepository.save(oldWorkflow);
            
            // Tạo workflow mới
            Workflow newWorkflow = new Workflow();
            newWorkflow.setId(UUID.randomUUID());
            newWorkflow.setWorkflowType(oldWorkflow.getWorkflowType());
            newWorkflow.setDescription(request.getDescription() != null ? request.getDescription() : oldWorkflow.getDescription());
            newWorkflow.setVersion(newVersion);
            newWorkflow.setWorkflowDiagram(normalizedBpmn);
            newWorkflow.setListPolicy(policies);
            newWorkflow.setStatus(request.getStatus() != null ? request.getStatus() : DataStatus.ACTIVE);
            newWorkflow.setCreatedBy(userId);
            newWorkflow.setUpdatedBy(userId);
            newWorkflow.setCreatedDate(LocalDateTime.now());
            newWorkflow.setUpdatedDate(LocalDateTime.now());
            
            // Xử lý force activate
            if (newWorkflow.getStatus() == DataStatus.ACTIVE) {
                Optional<Workflow> otherActive = workflowRepository.findActiveByType(newWorkflow.getWorkflowType());
                if (otherActive.isPresent() && !otherActive.get().getId().equals(oldWorkflow.getId())) {
                    if (!isForceActive) {
                        return new ResultMessage<>(ResultCode.PARAMS_ERROR,
                                "Đã có quy trình khác đang kích hoạt. Vui lòng chọn ép buộc kích hoạt.", null);
                    } else {
                        Workflow other = otherActive.get();
                        other.deactivate();
                        other.setUpdatedBy(userId);
                        workflowRepository.save(other);
                    }
                }
            }
            
            Workflow savedWorkflow = workflowRepository.save(newWorkflow);
            
            log.info("Created new workflow version: {} -> {} for type {}", 
                    oldWorkflow.getVersion(), newVersion, oldWorkflow.getWorkflowType());
            
            return new ResultMessage<>(ResultCode.SUCCESS, 
                    "Quy trình đã được sử dụng, đã tạo phiên bản mới: " + newVersion, 
                    WorkflowMapper.toDTO(savedWorkflow));
                    
        } catch (Exception e) {
            log.error("Error creating new workflow version: {}", e.getMessage(), e);
            return new ResultMessage<>(ResultCode.PARAMS_ERROR, "Tạo phiên bản mới thất bại: " + e.getMessage(), null);
        }
    }
    
    /**
     * Tăng version number (1.0 -> 1.1, 1.9 -> 2.0)
     */
    private String incrementVersion(String currentVersion) {
        if (currentVersion == null || currentVersion.isEmpty()) {
            return "1.1";
        }
        
        try {
            String[] parts = currentVersion.split("\\.");
            if (parts.length == 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                minor++;
                if (minor > 9) {
                    major++;
                    minor = 0;
                }
                return major + "." + minor;
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        
        return currentVersion + ".1";
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

            // 1. Lấy tất cả các thành phần
            NodeList startEventNodes = process.getElementsByTagNameNS(BPMN_NAMESPACE, "startEvent");
            NodeList endEventNodes = process.getElementsByTagNameNS(BPMN_NAMESPACE, "endEvent");
            NodeList taskNodes = process.getElementsByTagNameNS(BPMN_NAMESPACE, "task");
            NodeList sequenceFlowNodes = process.getElementsByTagNameNS(BPMN_NAMESPACE, "sequenceFlow");
            
            // Lấy tất cả gateways (exclusiveGateway, parallelGateway, inclusiveGateway...)
            List<Element> gateways = new ArrayList<>();
            String[] gatewayTypes = {"exclusiveGateway", "parallelGateway", "inclusiveGateway", "complexGateway"};
            for (String type : gatewayTypes) {
                NodeList gwNodes = process.getElementsByTagNameNS(BPMN_NAMESPACE, type);
                for (int i = 0; i < gwNodes.getLength(); i++) {
                    gateways.add((Element) gwNodes.item(i));
                }
            }

            // 2. Tạo Maps để tra cứu
            Map<String, String> allNodes = new HashMap<>(); // id -> nodeType
            
            for (int i = 0; i < startEventNodes.getLength(); i++) {
                Element e = (Element) startEventNodes.item(i);
                String id = e.getAttribute("id");
                if (id != null && !id.isEmpty()) allNodes.put(id, "startEvent");
            }
            for (int i = 0; i < endEventNodes.getLength(); i++) {
                Element e = (Element) endEventNodes.item(i);
                String id = e.getAttribute("id");
                if (id != null && !id.isEmpty()) allNodes.put(id, "endEvent");
            }
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Element e = (Element) taskNodes.item(i);
                String id = e.getAttribute("id");
                if (id != null && !id.isEmpty()) allNodes.put(id, "task");
            }
            for (Element gw : gateways) {
                String id = gw.getAttribute("id");
                if (id != null && !id.isEmpty()) allNodes.put(id, "gateway");
            }

            // Tạo lookup cho flows
            Map<String, List<Element>> flowsByTarget = new HashMap<>(); // targetRef -> flows going INTO this node
            Map<String, List<Element>> flowsBySource = new HashMap<>(); // sourceRef -> flows going OUT of this node
            
            for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
                Element flow = (Element) sequenceFlowNodes.item(i);
                String sourceRef = flow.getAttribute("sourceRef");
                String targetRef = flow.getAttribute("targetRef");
                
                flowsBySource.computeIfAbsent(sourceRef, k -> new ArrayList<>()).add(flow);
                flowsByTarget.computeIfAbsent(targetRef, k -> new ArrayList<>()).add(flow);
            }

            // 3. Validate Start Event
            if (startEventNodes.getLength() != 1) {
                result.addError("Quy trình phải có CHÍNH XÁC 1 Start Event (hiện có " + startEventNodes.getLength() + ")");
            } else {
                Element startEvent = (Element) startEventNodes.item(0);
                String startId = startEvent.getAttribute("id");
                
                // Start Event không được có luồng đi vào
                if (flowsByTarget.containsKey(startId) && !flowsByTarget.get(startId).isEmpty()) {
                    result.addError("Start Event không được có luồng đi vào");
                }
                // Start Event phải có ít nhất 1 luồng đi ra
                if (!flowsBySource.containsKey(startId) || flowsBySource.get(startId).isEmpty()) {
                    result.addError("Start Event phải có ÍT NHẤT 1 luồng đi ra");
                }
            }

            // 4. Validate End Event
            if (endEventNodes.getLength() != 1) {
                result.addError("Quy trình phải có CHÍNH XÁC 1 End Event (hiện có " + endEventNodes.getLength() + ")");
            } else {
                Element endEvent = (Element) endEventNodes.item(0);
                String endId = endEvent.getAttribute("id");
                
                // End Event không được có luồng đi ra
                if (flowsBySource.containsKey(endId) && !flowsBySource.get(endId).isEmpty()) {
                    result.addError("End Event không được có luồng đi ra");
                }
                // End Event phải có ít nhất 1 luồng đi vào
                if (!flowsByTarget.containsKey(endId) || flowsByTarget.get(endId).isEmpty()) {
                    result.addError("End Event phải có ÍT NHẤT 1 luồng đi vào");
                }
            }

            // 5. Validate Tasks
            if (taskNodes.getLength() < 1) {
                result.addError("Quy trình phải có ít nhất 1 Task");
            }
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Element task = (Element) taskNodes.item(i);
                String id = task.getAttribute("id");
                String name = task.getAttribute("name");
                if (name == null || name.isEmpty()) name = id;
                
                List<Element> incomingFlows = flowsByTarget.getOrDefault(id, Collections.emptyList());
                List<Element> outgoingFlows = flowsBySource.getOrDefault(id, Collections.emptyList());
                
                // Task phải có ít nhất 1 luồng vào (trừ task ngay sau StartEvent)
                if (incomingFlows.isEmpty()) {
                    result.addError("Task '" + name + "' phải có ÍT NHẤT 1 luồng vào");
                }
                
                // Task phải có chính xác 1 luồng ra
                if (outgoingFlows.size() != 1) {
                    result.addError("Task '" + name + "' phải có CHÍNH XÁC 1 luồng ra (hiện có " + outgoingFlows.size() + ")");
                }
            }

            // 6. Validate Gateways
            for (Element gateway : gateways) {
                String id = gateway.getAttribute("id");
                String name = gateway.getAttribute("name");
                if (name == null || name.isEmpty()) name = id;
                
                List<Element> incomingFlows = flowsByTarget.getOrDefault(id, Collections.emptyList());
                List<Element> outgoingFlows = flowsBySource.getOrDefault(id, Collections.emptyList());
                
                // Gateway phải có chính xác 1 luồng vào
                if (incomingFlows.size() != 1) {
                    result.addError("Điều kiện '" + name + "' phải có CHÍNH XÁC 1 luồng vào (hiện có " + incomingFlows.size() + ")");
                }
                
                // Gateway phải có chính xác 2 luồng ra
                if (outgoingFlows.size() != 2) {
                    result.addError("Điều kiện '" + name + "' phải có CHÍNH XÁC 2 luồng ra (hiện có " + outgoingFlows.size() + ")");
                }
                
                // Kiểm tra đầu vào phải là Task
                if (incomingFlows.size() == 1) {
                    String sourceId = incomingFlows.get(0).getAttribute("sourceRef");
                    String sourceType = allNodes.get(sourceId);
                    if (!"task".equals(sourceType)) {
                        result.addError("Điều kiện '" + name + "' phải có đầu vào là Task (hiện tại là '" + (sourceType != null ? sourceType : "Không rõ") + "')");
                    }
                }
                
                // Kiểm tra đầu ra phải là Task hoặc EndEvent
                for (Element flow : outgoingFlows) {
                    String targetId = flow.getAttribute("targetRef");
                    String targetType = allNodes.get(targetId);
                    if (!"task".equals(targetType) && !"endEvent".equals(targetType)) {
                        result.addError("Điều kiện '" + name + "' phải có đầu ra là Task hoặc End Event (hiện tại là '" + (targetType != null ? targetType : "Không rõ") + "')");
                    }
                }
                
                // Kiểm tra 2 luồng ra phải có action "yes" và "no"
                if (outgoingFlows.size() == 2) {
                    boolean hasYes = false;
                    boolean hasNo = false;
                    
                    for (Element flow : outgoingFlows) {
                        String action = flow.getAttribute("action");
                        if (action != null) {
                            action = action.toLowerCase();
                            if ("yes".equals(action) || "approve".equals(action)) {
                                hasYes = true;
                            } else if ("no".equals(action) || "reject".equals(action)) {
                                hasNo = true;
                            }
                        }
                    }
                    
                    if (!hasYes || !hasNo) {
                        result.addError("Điều kiện '" + name + "' phải có 2 luồng ra được gán hành động 'Duyệt' (yes) và 'Từ chối' (no)");
                    }
                }
            }

            // 7. Check connectivity - có ít nhất 1 sequence flow
            if (sequenceFlowNodes.getLength() < 1) {
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

    /**
     * Fixed Start Event ID để dễ query và kiểm tra workflow bắt đầu
     */
    public static final String WORKFLOW_START_EVENT_ID = "StartEvent_Begin";
    
    /**
     * Fixed End Event ID để dễ query và kiểm tra workflow hoàn thành
     */
    public static final String WORKFLOW_END_EVENT_ID = "EndEvent_Completed";

    /**
     * Chuẩn hóa StartEvent và EndEvent ID trong BPMN XML
     * Replace tất cả startEvent id thành WORKFLOW_START_EVENT_ID
     * Replace tất cả endEvent id thành WORKFLOW_END_EVENT_ID
     * Và cập nhật các SequenceFlow sourceRef/targetRef tương ứng
     */
    private String normalizeEndEventId(String bpmnXml) {
        if (bpmnXml == null || bpmnXml.isEmpty()) {
            return bpmnXml;
        }
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(bpmnXml)));
            
            // Map để lưu oldId -> newId cho cả start và end events
            Map<String, String> idReplacements = new HashMap<>();
            
            // Tìm và normalize startEvent
            NodeList startEvents = doc.getElementsByTagNameNS(BPMN_NAMESPACE, "startEvent");
            for (int i = 0; i < startEvents.getLength(); i++) {
                Element startEvent = (Element) startEvents.item(i);
                String oldId = startEvent.getAttribute("id");
                if (oldId != null && !oldId.isEmpty() && !oldId.equals(WORKFLOW_START_EVENT_ID)) {
                    idReplacements.put(oldId, WORKFLOW_START_EVENT_ID);
                    startEvent.setAttribute("id", WORKFLOW_START_EVENT_ID);
                    log.info("Normalized startEvent id: {} -> {}", oldId, WORKFLOW_START_EVENT_ID);
                }
            }
            
            // Tìm và normalize endEvent
            NodeList endEvents = doc.getElementsByTagNameNS(BPMN_NAMESPACE, "endEvent");
            for (int i = 0; i < endEvents.getLength(); i++) {
                Element endEvent = (Element) endEvents.item(i);
                String oldId = endEvent.getAttribute("id");
                if (oldId != null && !oldId.isEmpty() && !oldId.equals(WORKFLOW_END_EVENT_ID)) {
                    idReplacements.put(oldId, WORKFLOW_END_EVENT_ID);
                    endEvent.setAttribute("id", WORKFLOW_END_EVENT_ID);
                    log.info("Normalized endEvent id: {} -> {}", oldId, WORKFLOW_END_EVENT_ID);
                }
            }
            
            // Cập nhật tất cả SequenceFlow sourceRef/targetRef
            if (!idReplacements.isEmpty()) {
                NodeList sequenceFlows = doc.getElementsByTagNameNS(BPMN_NAMESPACE, "sequenceFlow");
                for (int i = 0; i < sequenceFlows.getLength(); i++) {
                    Element seqFlow = (Element) sequenceFlows.item(i);
                    
                    // Check và update sourceRef (cho startEvent)
                    String sourceRef = seqFlow.getAttribute("sourceRef");
                    if (idReplacements.containsKey(sourceRef)) {
                        seqFlow.setAttribute("sourceRef", idReplacements.get(sourceRef));
                    }
                    
                    // Check và update targetRef (cho endEvent)
                    String targetRef = seqFlow.getAttribute("targetRef");
                    if (idReplacements.containsKey(targetRef)) {
                        seqFlow.setAttribute("targetRef", idReplacements.get(targetRef));
                    }
                }
                
                // Cập nhật bpmndi:BPMNShape bpmnElement
                NodeList shapes = doc.getElementsByTagName("bpmndi:BPMNShape");
                for (int i = 0; i < shapes.getLength(); i++) {
                    Element shape = (Element) shapes.item(i);
                    String bpmnElement = shape.getAttribute("bpmnElement");
                    if (idReplacements.containsKey(bpmnElement)) {
                        shape.setAttribute("bpmnElement", idReplacements.get(bpmnElement));
                    }
                }
                
                // Cập nhật bpmndi:BPMNEdge bpmnElement (cho các flow arrows)
                NodeList edges = doc.getElementsByTagName("bpmndi:BPMNEdge");
                for (int i = 0; i < edges.getLength(); i++) {
                    Element edge = (Element) edges.item(i);
                    String bpmnElement = edge.getAttribute("bpmnElement");
                    if (idReplacements.containsKey(bpmnElement)) {
                        edge.setAttribute("bpmnElement", idReplacements.get(bpmnElement));
                    }
                }
            }
            
            // Convert document back to string
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
            
        } catch (Exception e) {
            log.error("Error normalizing BPMN event IDs: {}", e.getMessage(), e);
            // Nếu có lỗi, return original
            return bpmnXml;
        }
    }
}

