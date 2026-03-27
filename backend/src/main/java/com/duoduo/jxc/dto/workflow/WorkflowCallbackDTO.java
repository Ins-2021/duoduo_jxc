package com.duoduo.jxc.dto.workflow;

import lombok.Data;

/**
 * 工作流回调DTO
 */
@Data
public class WorkflowCallbackDTO {

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 业务类型（SALES_ORDER/PURCHASE_ORDER等）
     */
    private String bizType;

    /**
     * 业务单据ID
     */
    private String bizId;

    /**
     * 事件类型（PROCESS_COMPLETED/TASK_APPROVED/TASK_REJECTED）
     */
    private String eventType;

    /**
     * 审批人ID
     */
    private String approverId;

    /**
     * 审批意见
     */
    private String comment;
}
