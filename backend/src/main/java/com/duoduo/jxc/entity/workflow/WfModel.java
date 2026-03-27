package com.duoduo.jxc.entity.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_wf_model")
public class WfModel extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String modelKey;

    private String name;

    private String category;

    private String bpmnXml;

    private Integer version;

    private String status;
}

