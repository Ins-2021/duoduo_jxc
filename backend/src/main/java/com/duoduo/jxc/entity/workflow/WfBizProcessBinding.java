package com.duoduo.jxc.entity.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_wf_biz_process_binding")
public class WfBizProcessBinding extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private String processDefKey;

    private Integer enabled;

    private String startCondition;
}

