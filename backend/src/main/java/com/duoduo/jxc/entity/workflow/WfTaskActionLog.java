package com.duoduo.jxc.entity.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_wf_task_action_log")
public class WfTaskActionLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String procInstId;

    private String taskId;

    private String action;

    private Long operatorId;

    private String comment;

    private String attachmentsJson;

    private LocalDateTime createdAt;
}

