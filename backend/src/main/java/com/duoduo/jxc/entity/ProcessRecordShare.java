package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_record_share")
public class ProcessRecordShare extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long shareId;
    private Long recordId;
    private Long workerId;
    private BigDecimal shareRatio;
    private BigDecimal amount;
}
