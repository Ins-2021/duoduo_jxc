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
@TableName("jxc_wf_instance")
public class WfInstance extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String procInstId;

    private String businessKey;

    private String bizType;

    private String bizId;

    private String title;

    private Long initiatorId;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String currentTaskNames;
}

