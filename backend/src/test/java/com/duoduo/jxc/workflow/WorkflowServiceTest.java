package com.duoduo.jxc.workflow;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.workflow.*;
import com.duoduo.jxc.entity.workflow.WfModel;
import com.duoduo.jxc.entity.workflow.WfModelPublish;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.service.WorkflowService;
import org.flowable.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
public class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TaskService taskService;

    @Test
    void singleNodeHappyPath_shouldFinishAndReturnDiagram() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String bizId = "1";

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        workflowService.validateModel(modelId);

        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding(bizType, bindingSaveRequest(pub.getProcessDefKey(), 1), operatorId);

        Map<String, Object> started = workflowService.startInstance(startRequest(bizType, bizId, operatorId, "单节点审批"));
        String procInstId = String.valueOf(started.get("procInstId"));
        String businessKey = String.valueOf(started.get("businessKey"));

        String taskId = awaitTodoTask(operatorId, procInstId).get("taskId").toString();
        workflowService.approveTask(taskId, approveRequest(operatorId, "同意"));

        String xml = workflowService.getDiagramXml(procInstId);
        assertNotNull(xml);
        assertTrue(xml.contains("<definitions"));

        WfDiagramStateResponse state = workflowService.getDiagramState(procInstId);
        assertNotNull(state);
        assertNotNull(state.getActiveNodeKeys());
        assertNotNull(state.getFinishedNodeKeys());
        assertTrue(state.getActiveNodeKeys().isEmpty());
        assertTrue(state.getFinishedNodeKeys().contains("UserTask_1"));
    }

    @Test
    void twoNodeFlow_rejectToPrev_shouldReturnToFirstTask() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String bizId = "2";

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        WfModel model = workflowService.getModel(modelId);

        WfModelSaveRequest save = new WfModelSaveRequest();
        save.setBpmnXml(twoTaskBpmn(modelKey, List.of(operatorId), List.of(operatorId)));
        workflowService.saveModel(modelId, save, operatorId);
        workflowService.validateModel(modelId);

        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding(bizType, bindingSaveRequest(pub.getProcessDefKey(), 1), operatorId);

        Map<String, Object> started = workflowService.startInstance(startRequest(bizType, bizId, operatorId, "两节点审批"));
        String procInstId = String.valueOf(started.get("procInstId"));
        String businessKey = String.valueOf(started.get("businessKey"));

        Map<String, Object> t1 = awaitTodoTask(operatorId, procInstId);
        assertEquals("UserTask_1", String.valueOf(t1.get("nodeKey")));
        workflowService.approveTask(String.valueOf(t1.get("taskId")), approveRequest(operatorId, "同意1"));

        Map<String, Object> t2 = awaitTodoTask(operatorId, procInstId);
        assertEquals("UserTask_2", String.valueOf(t2.get("nodeKey")));
        WfTaskRejectRequest reject = new WfTaskRejectRequest();
        reject.setUserId(operatorId);
        reject.setRejectType("TO_PREV");
        reject.setComment("退回上一节点");
        workflowService.rejectTask(String.valueOf(t2.get("taskId")), reject);

        Map<String, Object> t1Again = awaitTodoTask(operatorId, procInstId);
        assertEquals("UserTask_1", String.valueOf(t1Again.get("nodeKey")));

        workflowService.approveTask(String.valueOf(t1Again.get("taskId")), approveRequest(operatorId, "同意1-2"));
        Map<String, Object> t2Again = awaitTodoTask(operatorId, procInstId);
        assertEquals("UserTask_2", String.valueOf(t2Again.get("nodeKey")));
        workflowService.approveTask(String.valueOf(t2Again.get("taskId")), approveRequest(operatorId, "同意2"));

        WfDiagramStateResponse state = workflowService.getDiagramState(procInstId);
        assertNotNull(state);
        assertTrue(state.getActiveNodeKeys().isEmpty());
        assertTrue(state.getFinishedNodeKeys().contains("UserTask_1"));
        assertTrue(state.getFinishedNodeKeys().contains("UserTask_2"));
    }

    @Test
    void startInstance_withoutBinding_shouldThrow() {
        WfInstanceStartRequest req = startRequest("UNBOUND", "3", 1001L, "未绑定");
        BusinessException ex = assertThrows(BusinessException.class, () -> workflowService.startInstance(req));
        assertTrue(ex.getMessage().contains("未绑定"));
    }

    @Test
    void approveTask_notCandidate_shouldThrow() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String bizId = "4";

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding(bizType, bindingSaveRequest(pub.getProcessDefKey(), 1), operatorId);
        Map<String, Object> started = workflowService.startInstance(startRequest(bizType, bizId, operatorId, "权限校验"));
        String businessKey = String.valueOf(started.get("businessKey"));

        String procInstId = String.valueOf(started.get("procInstId"));
        String taskId = awaitTodoTask(operatorId, procInstId).get("taskId").toString();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> workflowService.approveTask(taskId, approveRequest(2002L, "越权同意")));
        assertTrue(ex.getMessage().contains("无权限") || ex.getMessage().contains("不存在"));
    }

    @Test
    void approveTask_claimedByOther_shouldThrow() {
        long u1 = 1001L;
        long u2 = 2002L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String bizId = "5";

        Long modelId = workflowService.createModel(modelCreate(modelKey), u1);
        WfModelSaveRequest save = new WfModelSaveRequest();
        save.setBpmnXml(singleTaskBpmn(modelKey, List.of(u1, u2)));
        workflowService.saveModel(modelId, save, u1);
        workflowService.validateModel(modelId);

        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(u1));
        workflowService.saveBinding(bizType, bindingSaveRequest(pub.getProcessDefKey(), 1), u1);
        Map<String, Object> started = workflowService.startInstance(startRequest(bizType, bizId, u1, "抢占签收"));
        String procInstId = String.valueOf(started.get("procInstId"));

        String taskId = awaitTodoTask(u1, procInstId).get("taskId").toString();
        taskService.claim(taskId, String.valueOf(u1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> workflowService.approveTask(taskId, approveRequest(u2, "尝试处理他人已签收任务")));
        assertTrue(ex.getMessage().contains("已被他人签收"));

        workflowService.approveTask(taskId, approveRequest(u1, "签收后同意"));
    }

    @Test
    void diagramXml_unknownInstance_shouldThrowBusinessException() {
        assertThrows(BusinessException.class, () -> workflowService.getDiagramXml("NOT_EXISTS"));
    }

    @Test
    void instanceDetail_unknownInstance_shouldThrowBusinessException() {
        assertThrows(BusinessException.class, () -> workflowService.getInstanceDetail("NOT_EXISTS"));
    }

    @Test
    void validateModel_noUserTask_shouldFail() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);

        WfModelSaveRequest save = new WfModelSaveRequest();
        save.setBpmnXml("""
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             targetNamespace="http://duoduo.jxc/workflow">
                  <process id="%s" name="无审批节点" isExecutable="true">
                    <startEvent id="StartEvent_1" name="开始"/>
                    <sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="EndEvent_1"/>
                    <endEvent id="EndEvent_1" name="结束"/>
                  </process>
                </definitions>
                """.formatted(modelKey));
        workflowService.saveModel(modelId, save, operatorId);

        BusinessException ex = assertThrows(BusinessException.class, () -> workflowService.validateModel(modelId));
        assertTrue(ex.getMessage().contains("未找到审批节点"));
    }

    @Test
    void performanceSmoke_startAndApproveManyInstances_shouldSucceed() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");
        String bizType = "BIZ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding(bizType, bindingSaveRequest(pub.getProcessDefKey(), 1), operatorId);

        long start = System.currentTimeMillis();
        IntStream.range(0, 30).forEach(i -> {
            Map<String, Object> started = workflowService.startInstance(startRequest(bizType, String.valueOf(i), operatorId, "批量-" + i));
            String procInstId = String.valueOf(started.get("procInstId"));
            String taskId = awaitTodoTask(operatorId, procInstId).get("taskId").toString();
            workflowService.approveTask(taskId, approveRequest(operatorId, "同意"));
        });
        long costMs = System.currentTimeMillis() - start;
        assertTrue(costMs < 10000);
    }

    private Map<String, Object> awaitTodoTask(long userId, String procInstId) {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            List<Map<String, Object>> todo = workflowService.getTodoTasks(userId);
            if (todo != null) {
                List<Map<String, Object>> matched = todo.stream()
                        .filter(it -> Objects.equals(String.valueOf(it.get("procInstId")), procInstId))
                        .toList();
                if (matched.size() == 1) {
                    return matched.getFirst();
                }
                if (matched.size() > 1) {
                    throw new AssertionError("预期单条待办，但实际为: " + matched.size());
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        throw new AssertionError("等待待办超时");
    }

    private WfModelCreateRequest modelCreate(String modelKey) {
        WfModelCreateRequest req = new WfModelCreateRequest();
        req.setModelKey(modelKey);
        req.setName("测试流程");
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

    private String singleTaskBpmn(String processKey, List<Long> approvers) {
        String json = com.alibaba.fastjson2.JSON.toJSONString(Map.of("approverUserIds", approvers));
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                             xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
                             xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
                             targetNamespace="http://duoduo.jxc/workflow">
                  <process id="%s" name="单节点" isExecutable="true">
                    <startEvent id="StartEvent_1" name="开始"/>
                    <sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="UserTask_1"/>
                    <userTask id="UserTask_1" name="审批1">
                      <documentation>dd-config:%s</documentation>
                    </userTask>
                    <sequenceFlow id="Flow_2" sourceRef="UserTask_1" targetRef="EndEvent_1"/>
                    <endEvent id="EndEvent_1" name="结束"/>
                  </process>
                  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="%s"/>
                  </bpmndi:BPMNDiagram>
                </definitions>
                """.formatted(processKey, escapeXmlText(json), processKey);
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
