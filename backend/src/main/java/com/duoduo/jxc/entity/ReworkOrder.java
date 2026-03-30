package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_rework_order")
public class ReworkOrder extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long reworkId;
    private String reworkNo;
    private Long checkId;
    private Long bundleId;
    private String status;
    private Long handlerId;
    private java.time.LocalDateTime handleTime;
    private String remark;
}
