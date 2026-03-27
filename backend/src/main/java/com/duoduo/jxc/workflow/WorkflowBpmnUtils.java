package com.duoduo.jxc.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.flowable.bpmn.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WorkflowBpmnUtils {

    public static String defaultBpmnXml(String processKey, String processName, Long operatorId) {
        String approverJson = JSON.toJSONString(Map.of("approverUserIds", operatorId == null ? List.of() : List.of(operatorId)));
        return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                             xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
                             xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
                             targetNamespace="http://duoduo.jxc/workflow">
                  <process id="%s" name="%s" isExecutable="true">
                    <startEvent id="StartEvent_1" name="开始"/>
                    <sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="UserTask_1"/>
                    <userTask id="UserTask_1" name="审批">
                      <documentation>dd-config:%s</documentation>
                    </userTask>
                    <sequenceFlow id="Flow_2" sourceRef="UserTask_1" targetRef="EndEvent_1"/>
                    <endEvent id="EndEvent_1" name="结束"/>
                  </process>
                  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="%s"/>
                  </bpmndi:BPMNDiagram>
                </definitions>
                """, escape(processKey), escape(processName), escapeXmlText(approverJson), escape(processKey));
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String escapeXmlText(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static Map<String, List<Long>> extractUserTaskApprovers(String bpmnXml) {
        Map<String, List<Long>> result = new LinkedHashMap<>();
        if (bpmnXml == null || bpmnXml.isBlank()) {
            return result;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
            NodeList all = doc.getElementsByTagNameNS("*", "userTask");
            for (int i = 0; i < all.getLength(); i++) {
                Element e = (Element) all.item(i);
                String id = e.getAttribute("id");
                List<Long> approvers = extractApproversFromDocumentation(e);
                result.put(id, approvers);
            }
            return result;
        } catch (Exception ignored) {
            return result;
        }
    }

    private static List<Long> extractApproversFromDocumentation(Element userTaskEl) {
        NodeList docs = userTaskEl.getElementsByTagNameNS("*", "documentation");
        for (int j = 0; j < docs.getLength(); j++) {
            Node n = docs.item(j);
            String text = n.getTextContent();
            if (text == null) {
                continue;
            }
            String trimmed = text.trim();
            if (!trimmed.startsWith("dd-config:")) {
                continue;
            }
            String json = trimmed.substring("dd-config:".length()).trim();
            if (json.isBlank()) {
                return List.of();
            }
            JSONObject obj = JSON.parseObject(json);
            List<Long> ids = new ArrayList<>();
            if (obj.containsKey("approverUserIds")) {
                for (Object v : obj.getJSONArray("approverUserIds")) {
                    if (v == null) {
                        continue;
                    }
                    try {
                        ids.add(Long.parseLong(String.valueOf(v)));
                    } catch (Exception ignored) {
                    }
                }
            }
            return ids;
        }
        return List.of();
    }

    public static String findFirstUserTaskId(BpmnModel model) {
        if (model == null) {
            return null;
        }
        org.flowable.bpmn.model.Process process = model.getMainProcess();
        if (process == null) {
            return null;
        }
        StartEvent start = null;
        for (FlowElement fe : process.getFlowElements()) {
            if (fe instanceof StartEvent se) {
                start = se;
                break;
            }
        }
        if (start == null) {
            return null;
        }

        Deque<String> q = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        for (SequenceFlow sf : start.getOutgoingFlows()) {
            q.add(sf.getTargetRef());
        }
        while (!q.isEmpty()) {
            String id = q.removeFirst();
            if (!visited.add(id)) {
                continue;
            }
            FlowElement fe = process.getFlowElement(id);
            if (fe == null) {
                continue;
            }
            if (fe instanceof UserTask ut) {
                return ut.getId();
            }
            if (fe instanceof FlowNode node) {
                for (SequenceFlow sf : node.getOutgoingFlows()) {
                    q.add(sf.getTargetRef());
                }
            }
        }
        return null;
    }
}
