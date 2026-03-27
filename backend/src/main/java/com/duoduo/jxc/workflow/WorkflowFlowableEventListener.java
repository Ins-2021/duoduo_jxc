package com.duoduo.jxc.workflow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.workflow.WfInstance;
import com.duoduo.jxc.entity.workflow.WfTask;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.mapper.WfInstanceMapper;
import com.duoduo.jxc.mapper.WfTaskMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WorkflowFlowableEventListener implements FlowableEventListener {

    private final ObjectProvider<org.flowable.engine.RuntimeService> runtimeServiceProvider;
    private final ObjectProvider<org.flowable.engine.TaskService> taskServiceProvider;
    private final ObjectProvider<org.flowable.engine.RepositoryService> repositoryServiceProvider;
    private final WfTaskMapper wfTaskMapper;
    private final WfInstanceMapper wfInstanceMapper;
    private final SalesOrderMapper salesOrderMapper;

    @Override
    public void onEvent(FlowableEvent event) {
        if (event.getType() == FlowableEngineEventType.TASK_CREATED) {
            handleTaskCreated(event);
        }
        if (event.getType() == FlowableEngineEventType.TASK_COMPLETED) {
            handleTaskCompleted(event);
        }
        if (event.getType() == FlowableEngineEventType.PROCESS_STARTED) {
            handleProcessStarted(event);
        }
        if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED) {
            handleProcessCompleted(event);
        }
    }

    private void handleTaskCreated(FlowableEvent event) {
        if (!(event instanceof FlowableEntityEvent entityEvent)) {
            return;
        }
        if (!(entityEvent.getEntity() instanceof TaskEntity task)) {
            return;
        }

        org.flowable.engine.RuntimeService runtimeService = runtimeServiceProvider.getObject();
        org.flowable.engine.TaskService taskService = taskServiceProvider.getObject();

        String procInstId = task.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (pi == null) {
            return;
        }

        String businessKey = pi.getBusinessKey();
        String bizType = "";
        String bizId = "";
        if (businessKey != null && businessKey.contains(":")) {
            String[] parts = businessKey.split(":", 2);
            bizType = parts[0];
            bizId = parts[1];
        }

        WfTask exist = wfTaskMapper.selectOne(new LambdaQueryWrapper<WfTask>()
                .eq(WfTask::getDeleted, 0)
                .eq(WfTask::getTaskId, task.getId()));
        if (exist == null) {
            WfTask wfTask = new WfTask();
            wfTask.setTaskId(task.getId());
            wfTask.setProcInstId(procInstId);
            wfTask.setBizType(bizType);
            wfTask.setBizId(bizId);
            wfTask.setNodeKey(task.getTaskDefinitionKey());
            wfTask.setNodeName(task.getName());
            if (task.getAssignee() != null && !task.getAssignee().isBlank()) {
                try {
                    wfTask.setAssigneeId(Long.parseLong(task.getAssignee()));
                } catch (Exception ignored) {
                }
            }
            wfTask.setStatus("todo");
            wfTaskMapper.insert(wfTask);
        }

        List<Long> approverUserIds = resolveApprovers(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (approverUserIds != null && !approverUserIds.isEmpty()) {
            for (Long uid : approverUserIds) {
                taskService.addCandidateUser(task.getId(), String.valueOf(uid));
            }
            WfTask row = exist != null ? exist : wfTaskMapper.selectOne(new LambdaQueryWrapper<WfTask>()
                    .eq(WfTask::getDeleted, 0)
                    .eq(WfTask::getTaskId, task.getId()));
            if (row != null) {
                row.setCandidateSummary("U:" + String.join(",", approverUserIds.stream().map(String::valueOf).toList()));
                wfTaskMapper.updateById(row);
            }
        }

        updateInstanceCurrentTasks(procInstId);
    }

    private void handleTaskCompleted(FlowableEvent event) {
        if (!(event instanceof FlowableEntityEvent entityEvent)) {
            return;
        }
        if (!(entityEvent.getEntity() instanceof TaskEntity task)) {
            return;
        }
        String procInstId = task.getProcessInstanceId();
        WfTask row = wfTaskMapper.selectOne(new LambdaQueryWrapper<WfTask>()
                .eq(WfTask::getDeleted, 0)
                .eq(WfTask::getTaskId, task.getId()));
        if (row != null && !Objects.equals(row.getStatus(), "done")) {
            row.setStatus("done");
            wfTaskMapper.updateById(row);
        }
        updateInstanceCurrentTasks(procInstId);
    }

    private List<Long> resolveApprovers(String processDefinitionId, String taskDefinitionKey) {
        org.flowable.engine.RepositoryService repositoryService = repositoryServiceProvider.getObject();
        try (InputStream is = repositoryService.getProcessModel(processDefinitionId)) {
            if (is == null) {
                return List.of();
            }
            String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, List<Long>> map = WorkflowBpmnUtils.extractUserTaskApprovers(xml);
            return map.getOrDefault(taskDefinitionKey, List.of());
        } catch (Exception e) {
            return List.of();
        }
    }

    private void handleProcessStarted(FlowableEvent event) {
        if (!(event instanceof FlowableEntityEvent entityEvent)) {
            return;
        }
        Object entity = entityEvent.getEntity();
        if (!(entity instanceof ProcessInstance pi)) {
            return;
        }
        org.flowable.engine.RuntimeService runtimeService = runtimeServiceProvider.getObject();
        String businessKey = pi.getBusinessKey();
        String bizType = "";
        String bizId = "";
        if (businessKey != null && businessKey.contains(":")) {
            String[] parts = businessKey.split(":", 2);
            bizType = parts[0];
            bizId = parts[1];
        }
        WfInstance exist = wfInstanceMapper.selectOne(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getProcInstId, pi.getId()));
        if (exist != null) {
            return;
        }
        Object initiator = runtimeService.getVariable(pi.getId(), "initiatorId");
        Long initiatorId = null;
        if (initiator != null) {
            try {
                initiatorId = Long.parseLong(String.valueOf(initiator));
            } catch (Exception ignored) {
            }
        }
        if (initiatorId == null) {
            initiatorId = 0L;
        }
        WfInstance inst = new WfInstance();
        inst.setProcInstId(pi.getId());
        inst.setBusinessKey(businessKey == null ? "" : businessKey);
        inst.setBizType(bizType);
        inst.setBizId(bizId);
        inst.setTitle(String.valueOf(runtimeService.getVariable(pi.getId(), "title")));
        inst.setInitiatorId(initiatorId);
        inst.setStatus("running");
        inst.setStartTime(LocalDateTime.now());
        wfInstanceMapper.insert(inst);
    }

    private void handleProcessCompleted(FlowableEvent event) {
        if (!(event instanceof FlowableEntityEvent entityEvent)) {
            return;
        }
        Object entity = entityEvent.getEntity();
        if (!(entity instanceof ProcessInstance pi)) {
            return;
        }
        WfInstance inst = wfInstanceMapper.selectOne(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getProcInstId, pi.getId()));
        if (inst == null) {
            return;
        }
        if (!Objects.equals(inst.getStatus(), "finished")) {
            inst.setStatus("finished");
            inst.setEndTime(LocalDateTime.now());
            inst.setCurrentTaskNames(null);
            wfInstanceMapper.updateById(inst);
        }

        if (Objects.equals(inst.getBizType(), "SALES_ORDER")) {
            try {
                Long orderId = Long.parseLong(inst.getBizId());
                SalesOrder order = new SalesOrder();
                order.setOrderId(orderId);
                order.setStatus(30);
                order.setAuditTime(LocalDateTime.now());
                salesOrderMapper.updateById(order);
            } catch (Exception ignored) {
            }
        }
    }

    private void updateInstanceCurrentTasks(String procInstId) {
        if (procInstId == null || procInstId.isBlank()) {
            return;
        }
        org.flowable.engine.TaskService taskService = taskServiceProvider.getObject();
        List<String> names = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .active()
                .list()
                .stream()
                .map(t -> t.getName() == null ? "" : t.getName())
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
        String joined = names.isEmpty() ? null : String.join(",", names);
        WfInstance inst = wfInstanceMapper.selectOne(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getProcInstId, procInstId));
        if (inst == null) {
            return;
        }
        if (!Objects.equals(inst.getCurrentTaskNames(), joined)) {
            inst.setCurrentTaskNames(joined);
            wfInstanceMapper.updateById(inst);
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
