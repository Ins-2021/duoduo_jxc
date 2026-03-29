package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process")
public class Process extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long processId;
    private String processCode;
    private String processName;
    private String processType;
    private java.math.BigDecimal standardPrice;
    private Integer status;
}
