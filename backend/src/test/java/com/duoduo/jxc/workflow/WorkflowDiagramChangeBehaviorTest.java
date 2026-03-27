package com.duoduo.jxc.workflow;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.workflow.WfBindingSaveRequest;
import com.duoduo.jxc.dto.workflow.WfInstanceStartRequest;
import com.duoduo.jxc.dto.workflow.WfModelCreateRequest;
import com.duoduo.jxc.dto.workflow.WfModelPublishRequest;
import com.duoduo.jxc.dto.workflow.WfModelSaveRequest;
import com.duoduo.jxc.dto.workflow.WfTaskApproveRequest;
import com.duoduo.jxc.entity.workflow.WfModelPublish;
import com.duoduo.jxc.service.WorkflowService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
public class WorkflowDiagramChangeBehaviorTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    void publishNewDiagram_shouldAffectNewInstances_only() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        WfModelPublish pubV1 = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding(bizType, bindingSaveRequest(pubV1.getProcessDefKey(), 1), operatorId);

        Map<String, Object> started1 = workflowService.startInstance(startRequest(bizType, "A1", operatorId, "变更前"));
        String procInstId1 = String.valueOf(started1.get("procInstId"));
        String taskId1 = awaitTodoTaskId(operatorId, procInstId1);
        workflowService.approveTask(taskId1, approveRequest(operatorId, "同意"));

        HistoricProcessInstance hpi1 = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInstId1)
                .singleResult();
        assertNotNull(hpi1);
        int version1 = repositoryService.getProcessDefinition(hpi1.getProcessDefinitionId()).getVersion();

        WfModelSaveRequest save = new WfModelSaveRequest();
        save.setBpmnXml(twoTaskBpmn(modelKey, List.of(operatorId), List.of(operatorId)));
        workflowService.saveModel(modelId, save, operatorId);
        WfModelPublish pubV2 = workflowService.publishModel(modelId, publishRequest(operatorId));

        assertEquals(pubV1.getProcessDefKey(), pubV2.getProcessDefKey());
        assertTrue(pubV2.getProcessDefVersion() > pubV1.getProcessDefVersion());

        Map<String, Object> started2 = workflowService.startInstance(startRequest(bizType, "B1", operatorId, "变更后"));
        String procInstId2 = String.valueOf(started2.get("procInstId"));

        ProcessInstance pi2 = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId2).singleResult();
        assertNotNull(pi2);
        int version2 = repositoryService.getProcessDefinition(pi2.getProcessDefinitionId()).getVersion();

        assertTrue(version2 >= pubV2.getProcessDefVersion());
        assertTrue(version2 > version1);
    }

    private String awaitTodoTaskId(long userId, String procInstId) {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            List<Map<String, Object>> todo = workflowService.getTodoTasks(userId);
            if (todo != null) {
                List<Map<String, Object>> matched = todo.stream()
                        .filter(it -> Objects.equals(String.valueOf(it.get("procInstId")), procInstId))
                        .toList();
                if (matched.size() == 1) {
                    return String.valueOf(matched.getFirst().get("taskId"));
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        throw new AssertionError("等待待办超时: " + procInstId);
    }

    private WfModelCreateRequest modelCreate(String modelKey) {
        WfModelCreateRequest req = new WfModelCreateRequest();
        req.setModelKey(modelKey);
        req.setName("流程图变更测试");
        req.setCategory("TEST");
        return req;
    }

    private WfModelPublishRequest publishRequest(long operatorId) {
        WfModelPublishRequest req = new WfModelPublishRequest();
        req.setPublishedBy(operatorId);
        req.setRemark("test");
        return req;
    }

    private WfBindingSaveRequest bindingSaveRequest(String processDefKey, Integer enabled) {
        WfBindingSaveRequest req = new WfBindingSaveRequest();
        req.setProcessDefKey(processDefKey);
        req.setEnabled(enabled);
        return req;
    }

    private WfInstanceStartRequest startRequest(String bizType, String bizId, long initiatorId, String title) {
        WfInstanceStartRequest req = new WfInstanceStartRequest();
        req.setBizType(bizType);
        req.setBizId(bizId);
        req.setTitle(title);
        req.setInitiatorId(initiatorId);
        return req;
    }

    private WfTaskApproveRequest approveRequest(long userId, String comment) {
        WfTaskApproveRequest req = new WfTaskApproveRequest();
        req.setUserId(userId);
        req.setComment(comment);
        return req;
    }

    private String twoTaskBpmn(String processKey, List<Long> approvers1, List<Long> approvers2) {
        String json1 = com.alibaba.fastjson2.JSON.toJSONString(Map.of("approverUserIds", approvers1));
        String json2 = com.alibaba.fastjson2.JSON.toJSONString(Map.of("approverUserIds", approvers2));
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                             xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
                             xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
                             targetNamespace="http://duoduo.jxc/workflow">
                  <process id="%s" name="两节点" isExecutable="true">
                    <startEvent id="StartEvent_1" name="开始"/>
                    <sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="UserTask_1"/>
                    <userTask id="UserTask_1" name="审批1">
                      <documentation>dd-config:%s</documentation>
                    </userTask>
                    <sequenceFlow id="Flow_2" sourceRef="UserTask_1" targetRef="UserTask_2"/>
                    <userTask id="UserTask_2" name="审批2">
                      <documentation>dd-config:%s</documentation>
                    </userTask>
                    <sequenceFlow id="Flow_3" sourceRef="UserTask_2" targetRef="EndEvent_1"/>
                    <endEvent id="EndEvent_1" name="结束"/>
                  </process>
                  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="%s"/>
                  </bpmndi:BPMNDiagram>
                </definitions>
                """.formatted(processKey, escapeXmlText(json1), escapeXmlText(json2), processKey);
    }

    private String escapeXmlText(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

