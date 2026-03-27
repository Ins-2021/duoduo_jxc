package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.workflow.WorkflowCallbackDTO;

/**
 * 工作流回调服务接口
 */
public interface WorkflowCallbackService {

    /**
     * 流程实例结束回调
     *
     * @param dto 回调参数
     */
    void onProcessCompleted(WorkflowCallbackDTO dto);

    /**
     * 任务通过回调
     *
     * @param dto 回调参数
     */
    void onTaskApproved(WorkflowCallbackDTO dto);

    /**
     * 任务驳回回调
     *
     * @param dto 回调参数
     */
    void onTaskRejected(WorkflowCallbackDTO dto);
}
