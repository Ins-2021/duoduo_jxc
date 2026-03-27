package com.duoduo.jxc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.workflow.*;
import com.duoduo.jxc.entity.workflow.*;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.*;
import com.duoduo.jxc.service.WorkflowService;
import com.duoduo.jxc.workflow.WorkflowBpmnUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WfModelMapper wfModelMapper;
    private final WfModelPublishMapper wfModelPublishMapper;
    private final WfBizProcessBindingMapper wfBizProcessBindingMapper;
    private final WfInstanceMapper wfInstanceMapper;
    private final WfTaskActionLogMapper wfTaskActionLogMapper;

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @Override
    public List<WfModel> listModels() {
        return wfModelMapper.selectList(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getDeleted, 0)
                .orderByDesc(WfModel::getId));
    }

    @Override
    public Long createModel(WfModelCreateRequest request, Long operatorId) {
        WfModel exist = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getDeleted, 0)
                .eq(WfModel::getModelKey, request.getModelKey()));
        if (exist != null) {
            throw new BusinessException("流程Key已存在");
        }

        String xml = WorkflowBpmnUtils.defaultBpmnXml(request.getModelKey(), request.getName(), operatorId);
        WfModel model = new WfModel();
        model.setModelKey(request.getModelKey());
        model.setName(request.getName());
        model.setCategory(request.getCategory());
        model.setBpmnXml(xml);
        model.setVersion(1);
        model.setStatus("draft");
        wfModelMapper.insert(model);
        return model.getId();
    }

    @Override
    public WfModel getModel(Long id) {
        WfModel model = wfModelMapper.selectById(id);
        if (model == null || Objects.equals(model.getDeleted(), 1)) {
            throw new BusinessException("流程模型不存在");
        }
        return model;
    }

    @Override
    public void saveModel(Long id, WfModelSaveRequest request, Long operatorId) {
        WfModel model = getModel(id);
        model.setBpmnXml(request.getBpmnXml());
        if (request.getName() != null && !request.getName().isBlank()) {
            model.setName(request.getName());
        }
        if (request.getCategory() != null) {
            model.setCategory(request.getCategory());
        }
        model.setVersion(model.getVersion() == null ? 1 : model.getVersion() + 1);
        wfModelMapper.updateById(model);
    }

    @Override
    public void validateModel(Long id) {
        WfModel model = getModel(id);
        Map<String, List<Long>> taskApprovers = WorkflowBpmnUtils.extractUserTaskApprovers(model.getBpmnXml());
        if (taskApprovers.isEmpty()) {
            throw new BusinessException("流程中未找到审批节点");
        }
        for (Map.Entry<String, List<Long>> e : taskApprovers.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                throw new BusinessException("审批节点未配置审批人: " + e.getKey());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WfModelPublish publishModel(Long id, WfModelPublishRequest request) {
        WfModel model = getModel(id);
        validateModel(id);

        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getModelKey())
                .addString(model.getModelKey() + ".bpmn20.xml", model.getBpmnXml())
                .deploy();

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .orderByProcessDefinitionVersion()
                .desc()
                .list()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("发布失败：未找到流程定义"));

        WfModelPublish pub = new WfModelPublish();
        pub.setModelId(model.getId());
        pub.setDeploymentId(deployment.getId());
        pub.setProcessDefId(pd.getId());
        pub.setProcessDefKey(pd.getKey());
        pub.setProcessDefVersion(pd.getVersion());
        pub.setPublishedBy(request == null ? null : request.getPublishedBy());
        pub.setPublishedAt(LocalDateTime.now());
        pub.setRemark(request == null ? null : request.getRemark());
        wfModelPublishMapper.insert(pub);

        model.setStatus("published");
        wfModelMapper.updateById(model);
        return pub;
    }

    @Override
    public WfBizProcessBinding getBinding(String bizType) {
        return wfBizProcessBindingMapper.selectOne(new LambdaQueryWrapper<WfBizProcessBinding>()
                .eq(WfBizProcessBinding::getDeleted, 0)
                .eq(WfBizProcessBinding::getBizType, bizType));
    }

    @Override
    public void saveBinding(String bizType, WfBindingSaveRequest request, Long operatorId) {
        WfBizProcessBinding binding = getBinding(bizType);
        if (binding == null) {
            binding = new WfBizProcessBinding();
            binding.setBizType(bizType);
            binding.setProcessDefKey(request.getProcessDefKey());
            binding.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
            binding.setStartCondition(request.getStartCondition());
            wfBizProcessBindingMapper.insert(binding);
            return;
        }
        binding.setProcessDefKey(request.getProcessDefKey());
        if (request.getEnabled() != null) {
            binding.setEnabled(request.getEnabled());
        }
        binding.setStartCondition(request.getStartCondition());
        wfBizProcessBindingMapper.updateById(binding);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> startInstance(WfInstanceStartRequest request) {
        WfBizProcessBinding binding = getBinding(request.getBizType());
        if (binding == null || Objects.equals(binding.getEnabled(), 0)) {
            throw new BusinessException("该业务类型未绑定可用流程");
        }

        String businessKey = request.getBizType() + ":" + request.getBizId();

        ProcessInstance existPi = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .listPage(0, 1)
                .stream()
                .findFirst()
                .orElse(null);
        if (existPi != null) {
            return Map.of("procInstId", existPi.getId(), "businessKey", businessKey);
        }

        Map<String, Object> vars = request.getVariables() == null ? new HashMap<>() : new HashMap<>(request.getVariables());
        vars.put("initiatorId", request.getInitiatorId());
        vars.put("bizType", request.getBizType());
        vars.put("bizId", request.getBizId());
        vars.put("title", request.getTitle());

        ProcessInstance pi = runtimeService.startProcessInstanceByKey(binding.getProcessDefKey(), businessKey, vars);

        WfInstance exist = wfInstanceMapper.selectOne(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getProcInstId, pi.getId()));
        if (exist == null) {
            WfInstance inst = new WfInstance();
            inst.setProcInstId(pi.getId());
            inst.setBusinessKey(businessKey);
            inst.setBizType(request.getBizType());
            inst.setBizId(request.getBizId());
            inst.setTitle(request.getTitle());
            inst.setInitiatorId(request.getInitiatorId());
            inst.setStatus("running");
            inst.setStartTime(LocalDateTime.now());
            wfInstanceMapper.insert(inst);
        }

        return Map.of("procInstId", pi.getId(), "businessKey", businessKey);
    }

    @Override
    public List<Map<String, Object>> getTodoTasks(Long userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(String.valueOf(userId))
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Task t : tasks) {
            String businessKey = null;
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(t.getProcessInstanceId()).singleResult();
            if (pi != null) {
                businessKey = pi.getBusinessKey();
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taskId", t.getId());
            row.put("procInstId", t.getProcessInstanceId());
            row.put("taskName", t.getName());
            row.put("nodeKey", t.getTaskDefinitionKey());
            row.put("assignee", t.getAssignee());
            row.put("businessKey", businessKey);
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDoneTasks(Long userId) {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(String.valueOf(userId))
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (HistoricTaskInstance t : tasks) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taskId", t.getId());
            row.put("procInstId", t.getProcessInstanceId());
            row.put("taskName", t.getName());
            row.put("nodeKey", t.getTaskDefinitionKey());
            row.put("endTime", t.getEndTime());
            result.add(row);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(String taskId, WfTaskApproveRequest request) {
        Task task = requireTaskCanOperate(taskId, request.getUserId());

        if (task.getAssignee() == null || task.getAssignee().isBlank()) {
            taskService.claim(taskId, String.valueOf(request.getUserId()));
        }

        if (request.getComment() != null && !request.getComment().isBlank()) {
            taskService.addComment(taskId, task.getProcessInstanceId(), request.getComment());
        }

        Map<String, Object> vars = request.getVariables() == null ? new HashMap<>() : new HashMap<>(request.getVariables());
        vars.put("__action", "approve");
        taskService.complete(taskId, vars);

        WfTaskActionLog log = new WfTaskActionLog();
        log.setProcInstId(task.getProcessInstanceId());
        log.setTaskId(taskId);
        log.setAction("approve");
        log.setOperatorId(request.getUserId());
        log.setComment(request.getComment());
        log.setCreatedAt(LocalDateTime.now());
        wfTaskActionLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(String taskId, WfTaskRejectRequest request) {
        Task task = requireTaskCanOperate(taskId, request.getUserId());

        if (task.getAssignee() == null || task.getAssignee().isBlank()) {
            taskService.claim(taskId, String.valueOf(request.getUserId()));
        }

        if (request.getComment() != null && !request.getComment().isBlank()) {
            taskService.addComment(taskId, task.getProcessInstanceId(), request.getComment());
        }

        String rejectType = request.getRejectType() == null ? "TO_START" : request.getRejectType();
        String currentActivityId = task.getTaskDefinitionKey();
        String targetActivityId;

        if ("TO_PREV".equalsIgnoreCase(rejectType)) {
            HistoricActivityInstance prev = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .activityType("userTask")
                    .finished()
                    .orderByHistoricActivityInstanceEndTime()
                    .desc()
                    .listPage(0, 1)
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (prev == null) {
                throw new BusinessException("无法驳回到上一节点");
            }
            targetActivityId = prev.getActivityId();
        } else {
            BpmnModel model = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            targetActivityId = WorkflowBpmnUtils.findFirstUserTaskId(model);
            if (targetActivityId == null) {
                throw new BusinessException("无法定位首个审批节点");
            }
        }

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(currentActivityId, targetActivityId)
                .changeState();

        WfTaskActionLog log = new WfTaskActionLog();
        log.setProcInstId(task.getProcessInstanceId());
        log.setTaskId(taskId);
        log.setAction("reject");
        log.setOperatorId(request.getUserId());
        log.setComment(request.getComment());
        log.setCreatedAt(LocalDateTime.now());
        wfTaskActionLogMapper.insert(log);
    }

    @Override
    public List<WfInstance> listMyInstances(Long userId) {
        return wfInstanceMapper.selectList(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getInitiatorId, userId)
                .orderByDesc(WfInstance::getId));
    }

    @Override
    public Map<String, Object> getInstanceDetail(String procInstId) {
        WfInstance inst = wfInstanceMapper.selectOne(new LambdaQueryWrapper<WfInstance>()
                .eq(WfInstance::getDeleted, 0)
                .eq(WfInstance::getProcInstId, procInstId));
        if (inst == null) {
            throw new BusinessException("流程实例不存在");
        }

        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(procInstId)
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list();

        List<Map<String, Object>> active = new ArrayList<>();
        for (Task t : activeTasks) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taskId", t.getId());
            row.put("taskName", t.getName());
            row.put("nodeKey", t.getTaskDefinitionKey());
            row.put("assignee", t.getAssignee());
            active.add(row);
        }

        List<WfTaskActionLog> logs = wfTaskActionLogMapper.selectList(new LambdaQueryWrapper<WfTaskActionLog>()
                .eq(WfTaskActionLog::getDeleted, 0)
                .eq(WfTaskActionLog::getProcInstId, procInstId)
                .orderByAsc(WfTaskActionLog::getId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("instance", inst);
        result.put("activeTasks", active);
        result.put("actionLogs", logs);
        return result;
    }

    @Override
    public String getDiagramXml(String procInstId) {
        String defId;
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (pi != null) {
            defId = pi.getProcessDefinitionId();
        } else {
            var hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstId)
                    .singleResult();
            if (hpi == null) {
                throw new BusinessException("流程实例不存在");
            }
            defId = hpi.getProcessDefinitionId();
        }

        try (InputStream is = repositoryService.getProcessModel(defId)) {
            if (is == null) {
                throw new BusinessException("流程图不存在");
            }
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("读取流程图失败");
        }
    }

    @Override
    public WfDiagramStateResponse getDiagramState(String procInstId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        var hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (pi == null && hpi == null) {
            throw new BusinessException("流程实例不存在");
        }

        List<String> activeIds;
        try {
            activeIds = runtimeService.getActiveActivityIds(procInstId);
        } catch (Exception ignored) {
            activeIds = List.of();
        }
        List<String> finished = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInstId)
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .distinct()
                .toList();

        WfDiagramStateResponse resp = new WfDiagramStateResponse();
        resp.setActiveNodeKeys(activeIds);
        resp.setFinishedNodeKeys(finished);
        return resp;
    }

    private Task requireTaskCanOperate(String taskId, Long userId) {
        if (userId == null) {
            throw new BusinessException(BizCode.WORKFLOW_TASK_NOT_FOUND);
        }
        String uid = String.valueOf(userId);
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new BusinessException(BizCode.WORKFLOW_TASK_NOT_FOUND);
        }
        if (task.getAssignee() != null && !task.getAssignee().isBlank()) {
            if (!Objects.equals(task.getAssignee(), uid)) {
                throw new BusinessException(BizCode.WORKFLOW_TASK_ALREADY_CLAIMED);
            }
            return task;
        }
        Task cand = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(uid)
                .singleResult();
        if (cand == null) {
            throw new BusinessException(BizCode.WORKFLOW_TASK_NOT_FOUND);
        }
        return task;
    }
}
