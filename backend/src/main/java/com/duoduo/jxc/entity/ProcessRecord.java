package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_record")
public class ProcessRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long recordId;
    private Long workerId;
    private Long processId;
    private Long bundleId;
    private Integer quantity;
    private java.math.BigDecimal amount;
}
