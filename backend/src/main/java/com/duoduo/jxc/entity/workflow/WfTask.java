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
@TableName("jxc_wf_task")
public class WfTask extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskId;

    private String procInstId;

    private String bizType;

    private String bizId;

    private String nodeKey;

    private String nodeName;

    private Long assigneeId;

    private String candidateSummary;

    private LocalDateTime dueTime;

    private String status;
}

