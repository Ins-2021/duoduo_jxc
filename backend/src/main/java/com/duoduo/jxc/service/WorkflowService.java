package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.workflow.*;
import com.duoduo.jxc.entity.workflow.WfBizProcessBinding;
import com.duoduo.jxc.entity.workflow.WfInstance;
import com.duoduo.jxc.entity.workflow.WfModel;
import com.duoduo.jxc.entity.workflow.WfModelPublish;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

    List<WfModel> listModels();

    Long createModel(WfModelCreateRequest request, Long operatorId);

    WfModel getModel(Long id);

    void saveModel(Long id, WfModelSaveRequest request, Long operatorId);

    void validateModel(Long id);

    WfModelPublish publishModel(Long id, WfModelPublishRequest request);

    WfBizProcessBinding getBinding(String bizType);

    void saveBinding(String bizType, WfBindingSaveRequest request, Long operatorId);

    Map<String, Object> startInstance(WfInstanceStartRequest request);

    List<Map<String, Object>> getTodoTasks(Long userId);

    List<Map<String, Object>> getDoneTasks(Long userId);

    void approveTask(String taskId, WfTaskApproveRequest request);

    void rejectTask(String taskId, WfTaskRejectRequest request);

    List<WfInstance> listMyInstances(Long userId);

    Map<String, Object> getInstanceDetail(String procInstId);

    String getDiagramXml(String procInstId);

    WfDiagramStateResponse getDiagramState(String procInstId);
}
