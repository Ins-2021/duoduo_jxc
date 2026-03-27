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
@TableName("jxc_wf_model_publish")
public class WfModelPublish extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long modelId;

    private String deploymentId;

    private String processDefId;

    private String processDefKey;

    private Integer processDefVersion;

    private Long publishedBy;

    private LocalDateTime publishedAt;

    private String remark;
}

