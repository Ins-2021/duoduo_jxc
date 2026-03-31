package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bundle_flow")
public class BundleFlow extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long flowId;

    private Long bundleId;

    private String bundleNo;

    private String fromLocation;

    private String toLocation;

    private Long fromProcessId;

    private String fromProcessName;

    private Long toProcessId;

    private String toProcessName;

    private Long fromWorkerId;

    private String fromWorkerName;

    private Long toWorkerId;

    private String toWorkerName;

    private Integer quantity;

    private String flowType;

    private String status;

    private String remark;

    private LocalDateTime flowTime;
}
