package com.restaurant.ddd.application.util;

import com.restaurant.ddd.application.model.workflow.WorkflowStateDTO;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.InputSource;

/**
 * Utility class để parse và xử lý BPMN XML diagram
 */
public class WorkflowUtils {
    
    private static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    
    /**
     * Parse BPMN XML để lấy thông tin bước tiếp theo
     */
    public static WorkflowStateDTO getNextStepInfo(String bpmnXml, String currentStepId) {
        if (bpmnXml == null || bpmnXml.isEmpty()) {
            return null;
        }
        
        try {
            Document doc = parseXml(bpmnXml);
            Element process = getProcessElement(doc);
            if (process == null) return null;
            
            // Nếu currentStepId null, tìm StartEvent
            if (currentStepId == null || currentStepId.isEmpty()) {
                return getStartEventInfo(process);
            }
            
            // Tìm element hiện tại
            Element currentElement = getElementById(process, currentStepId);
            if (currentElement == null) return null;
            
            String stepType = getStepType(currentElement);
            String stepName = currentElement.getAttribute("name");
            
            // Lấy các sequence flows đi ra từ bước hiện tại
            List<WorkflowStateDTO.WorkflowActionOption> actions = getOutgoingActions(process, currentStepId);
            
            return WorkflowStateDTO.builder()
                    .currentStepId(currentStepId)
                    .currentStepName(stepName != null ? stepName : currentStepId)
                    .currentStepType(stepType)
                    .isEndStep("EndEvent".equals(stepType))
                    .isComplete("EndEvent".equals(stepType))
                    .availableActions(actions)
                    .build();
                    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy StartEvent info
     */
    public static WorkflowStateDTO getStartEventInfo(String bpmnXml) {
        try {
            Document doc = parseXml(bpmnXml);
            Element process = getProcessElement(doc);
            if (process == null) return null;
            return getStartEventInfo(process);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static WorkflowStateDTO getStartEventInfo(Element process) {
        NodeList startEvents = process.getElementsByTagNameNS(BPMN_NS, "startEvent");
        if (startEvents.getLength() == 0) {
            // Try without namespace
            startEvents = process.getElementsByTagName("bpmn:startEvent");
        }
        if (startEvents.getLength() == 0) {
            startEvents = process.getElementsByTagName("startEvent");
        }
        
        if (startEvents.getLength() > 0) {
            Element startEvent = (Element) startEvents.item(0);
            String stepId = startEvent.getAttribute("id");
            String stepName = startEvent.getAttribute("name");
            
            List<WorkflowStateDTO.WorkflowActionOption> actions = getOutgoingActions(process, stepId);
            
            return WorkflowStateDTO.builder()
                    .currentStepId(stepId)
                    .currentStepName(stepName != null && !stepName.isEmpty() ? stepName : "Bắt đầu")
                    .currentStepType("StartEvent")
                    .isEndStep(false)
                    .isComplete(false)
                    .availableActions(actions)
                    .build();
        }
        
        return null;
    }
    
    /**
     * Lấy các actions có thể thực hiện từ bước hiện tại
     * Nếu target là Gateway, sẽ hiển thị các actions của Gateway (Duyệt/Từ chối)
     */
    private static List<WorkflowStateDTO.WorkflowActionOption> getOutgoingActions(Element process, String sourceId) {
        List<WorkflowStateDTO.WorkflowActionOption> actions = new ArrayList<>();
        
        // Tìm tất cả sequence flows có source là bước hiện tại
        NodeList flows = process.getElementsByTagName("bpmn:sequenceFlow");
        if (flows.getLength() == 0) {
            flows = process.getElementsByTagNameNS(BPMN_NS, "sequenceFlow");
        }
        if (flows.getLength() == 0) {
            flows = process.getElementsByTagName("sequenceFlow");
        }
        
        for (int i = 0; i < flows.getLength(); i++) {
            Element flow = (Element) flows.item(i);
            String source = flow.getAttribute("sourceRef");
            
            if (sourceId.equals(source)) {
                String targetId = flow.getAttribute("targetRef");
                Element targetElement = getElementById(process, targetId);
                
                // Kiểm tra nếu target là Gateway -> lấy actions của Gateway
                if (targetElement != null && isGateway(targetElement)) {
                    // Lấy các flows đi ra từ Gateway
                    List<WorkflowStateDTO.WorkflowActionOption> gatewayActions = getGatewayActions(process, flows, targetId);
                    actions.addAll(gatewayActions);
                } else {
                    // Trường hợp bình thường - target không phải Gateway
                    String flowName = flow.getAttribute("name");
                    String actionKey = flow.getAttribute("id");
                    
                    // Nếu flow không có tên, lấy tên của target element
                    if (flowName == null || flowName.isEmpty()) {
                        if (targetElement != null) {
                            flowName = targetElement.getAttribute("name");
                            if (flowName == null || flowName.isEmpty()) {
                                flowName = getDefaultActionName(targetElement);
                            }
                        }
                    }
                    
                    actions.add(WorkflowStateDTO.WorkflowActionOption.builder()
                            .actionKey(actionKey)
                            .actionName(flowName != null ? flowName : "Tiếp tục")
                            .targetStepId(targetId)
                            .build());
                }
            }
        }
        
        return actions;
    }
    
    /**
     * Kiểm tra element có phải là Gateway không
     */
    private static boolean isGateway(Element element) {
        String tagName = element.getLocalName();
        if (tagName == null) tagName = element.getTagName();
        return tagName.contains("Gateway") || tagName.contains("gateway");
    }
    
    /**
     * Lấy các actions từ Gateway (Duyệt/Từ chối)
     */
    private static List<WorkflowStateDTO.WorkflowActionOption> getGatewayActions(Element process, NodeList allFlows, String gatewayId) {
        List<WorkflowStateDTO.WorkflowActionOption> actions = new ArrayList<>();
        
        for (int i = 0; i < allFlows.getLength(); i++) {
            Element flow = (Element) allFlows.item(i);
            String source = flow.getAttribute("sourceRef");
            
            if (gatewayId.equals(source)) {
                String targetId = flow.getAttribute("targetRef");
                String actionKey = flow.getAttribute("id");
                
                // Ưu tiên lấy từ attribute 'action', sau đó 'name'
                String actionAttr = flow.getAttribute("action");
                String flowName = flow.getAttribute("name");
                
                String actionName;
                if (actionAttr != null && !actionAttr.isEmpty()) {
                    // Convert action to display name
                    actionName = getActionDisplayName(actionAttr);
                } else if (flowName != null && !flowName.isEmpty()) {
                    actionName = flowName;
                } else {
                    // Lấy tên của target element
                    Element targetElement = getElementById(process, targetId);
                    if (targetElement != null) {
                        actionName = targetElement.getAttribute("name");
                        if (actionName == null || actionName.isEmpty()) {
                            actionName = getDefaultActionName(targetElement);
                        }
                    } else {
                        actionName = "Thực hiện";
                    }
                }
                
                actions.add(WorkflowStateDTO.WorkflowActionOption.builder()
                        .actionKey(actionKey)
                        .actionName(actionName)
                        .targetStepId(targetId)
                        .build());
            }
        }
        
        return actions;
    }
    
    /**
     * Convert action attribute to display name
     */
    private static String getActionDisplayName(String action) {
        if (action == null) return "Thực hiện";
        action = action.toLowerCase();
        switch (action) {
            case "yes":
            case "approve":
                return "Duyệt";
            case "no":
            case "reject":
                return "Từ chối";
            default:
                return action;
        }
    }
    
    private static String getDefaultActionName(Element element) {
        String tagName = element.getLocalName();
        if (tagName == null) tagName = element.getTagName();
        
        if (tagName.contains("endEvent") || tagName.equals("bpmn:endEvent")) {
            return "Hoàn thành";
        }
        if (tagName.contains("task") || tagName.contains("Task")) {
            return "Tiếp tục";
        }
        return "Chuyển bước";
    }
    
    private static String getStepType(Element element) {
        String tagName = element.getLocalName();
        if (tagName == null) tagName = element.getTagName();
        
        if (tagName.contains("startEvent")) return "StartEvent";
        if (tagName.contains("endEvent")) return "EndEvent";
        if (tagName.contains("exclusiveGateway")) return "ExclusiveGateway";
        if (tagName.contains("parallelGateway")) return "ParallelGateway";
        if (tagName.contains("task") || tagName.contains("Task")) return "Task";
        return "Unknown";
    }
    
    private static Element getElementById(Element process, String id) {
        NodeList allElements = process.getElementsByTagName("*");
        for (int i = 0; i < allElements.getLength(); i++) {
            Element elem = (Element) allElements.item(i);
            if (id.equals(elem.getAttribute("id"))) {
                return elem;
            }
        }
        return null;
    }
    
    private static Element getProcessElement(Document doc) {
        NodeList processes = doc.getElementsByTagNameNS(BPMN_NS, "process");
        if (processes.getLength() == 0) {
            processes = doc.getElementsByTagName("bpmn:process");
        }
        if (processes.getLength() == 0) {
            processes = doc.getElementsByTagName("process");
        }
        if (processes.getLength() > 0) {
            return (Element) processes.item(0);
        }
        return doc.getDocumentElement();
    }
    
    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
    
    /**
     * Lấy tên bước từ BPMN diagram
     */
    public static String getStepName(String bpmnXml, String stepId) {
        if (bpmnXml == null || stepId == null) return stepId;
        
        try {
            Document doc = parseXml(bpmnXml);
            Element process = getProcessElement(doc);
            Element element = getElementById(process, stepId);
            if (element != null) {
                String name = element.getAttribute("name");
                return name != null && !name.isEmpty() ? name : stepId;
            }
        } catch (Exception e) {
            // ignore
        }
        return stepId;
    }
    
    /**
     * Lấy danh sách policyId của một step từ BPMN diagram
     * @param bpmnXml BPMN XML
     * @param stepId ID của step cần lấy policy
     * @return Danh sách policyId dạng UUID string, hoặc empty list nếu không có
     */
    public static List<String> getStepPolicyIds(String bpmnXml, String stepId) {
        List<String> policyIds = new ArrayList<>();
        if (bpmnXml == null || stepId == null) return policyIds;
        
        try {
            Document doc = parseXml(bpmnXml);
            Element process = getProcessElement(doc);
            Element element = getElementById(process, stepId);
            if (element != null) {
                String policyIdAttr = element.getAttribute("policyId");
                if (policyIdAttr != null && !policyIdAttr.isEmpty()) {
                    // policyId có format: ["uuid1","uuid2"]
                    // Parse JSON-like string
                    policyIdAttr = policyIdAttr.replace("[", "").replace("]", "")
                            .replace("\"", "").replace("&quot;", "");
                    String[] ids = policyIdAttr.split(",");
                    for (String id : ids) {
                        String trimmed = id.trim();
                        if (!trimmed.isEmpty()) {
                            policyIds.add(trimmed);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return policyIds;
    }
}
