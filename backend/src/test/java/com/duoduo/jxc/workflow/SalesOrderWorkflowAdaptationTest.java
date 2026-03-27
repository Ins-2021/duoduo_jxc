package com.duoduo.jxc.workflow;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.workflow.WfBindingSaveRequest;
import com.duoduo.jxc.dto.workflow.WfModelCreateRequest;
import com.duoduo.jxc.dto.workflow.WfModelPublishRequest;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.workflow.WfModelPublish;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.SalesOrderService;
import com.duoduo.jxc.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
public class SalesOrderWorkflowAdaptationTest {

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private WorkflowService workflowService;

    @Test
    void auditOrder_withWorkflowBinding_shouldStartInstance_andOnFinishSetRunning() {
        long operatorId = 1001L;
        String modelKey = "wf_" + UUID.randomUUID().toString().replace("-", "");

        Long modelId = workflowService.createModel(modelCreate(modelKey), operatorId);
        WfModelPublish pub = workflowService.publishModel(modelId, publishRequest(operatorId));
        workflowService.saveBinding("SALES_ORDER", bindingSaveRequest(pub.getProcessDefKey(), 1), operatorId);

        SalesOrder order = new SalesOrder();
        order.setDocNo("XS" + System.currentTimeMillis());
        order.setOrderType(1);
        order.setDocDate(LocalDate.now());
        order.setOperatorId(operatorId);
        order.setStatus(10);
        salesOrderMapper.insert(order);
        assertNotNull(order.getOrderId());

        salesOrderService.auditOrder(order.getOrderId());
        SalesOrder afterAudit = salesOrderMapper.selectById(order.getOrderId());
        assertNotNull(afterAudit);
        assertEquals(20, afterAudit.getStatus());

        String businessKey = "SALES_ORDER:" + order.getOrderId();
        String taskId = awaitTodoTaskId(operatorId, businessKey);
        workflowService.approveTask(taskId, approveRequest(operatorId, "同意"));

        SalesOrder afterFinish = awaitOrderStatus(order.getOrderId(), 30);
        assertEquals(30, afterFinish.getStatus());
    }

    private SalesOrder awaitOrderStatus(Long orderId, int status) {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            SalesOrder row = salesOrderMapper.selectById(orderId);
            if (row != null && row.getStatus() != null && row.getStatus() == status) {
                return row;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        SalesOrder row = salesOrderMapper.selectById(orderId);
        throw new AssertionError("等待单据状态超时，当前状态=" + (row == null ? null : row.getStatus()));
    }

    private String awaitTodoTaskId(long userId, String businessKey) {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            List<Map<String, Object>> todo = workflowService.getTodoTasks(userId);
            if (todo != null) {
                var matched = todo.stream()
                        .filter(it -> Objects.equals(String.valueOf(it.get("businessKey")), businessKey))
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
        throw new AssertionError("等待待办超时: " + businessKey);
    }

    private WfModelCreateRequest modelCreate(String modelKey) {
        WfModelCreateRequest req = new WfModelCreateRequest();
        req.setModelKey(modelKey);
        req.setName("销售单审批");
        req.setCategory("SALES");
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

    private com.duoduo.jxc.dto.workflow.WfTaskApproveRequest approveRequest(long userId, String comment) {
        com.duoduo.jxc.dto.workflow.WfTaskApproveRequest req = new com.duoduo.jxc.dto.workflow.WfTaskApproveRequest();
        req.setUserId(userId);
        req.setComment(comment);
        return req;
    }
}

