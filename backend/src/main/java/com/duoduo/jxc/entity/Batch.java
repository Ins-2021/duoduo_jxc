package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 批次管理实体
 */
@Data
@TableName("jxc_batch")
public class Batch {
    @TableId(type = IdType.AUTO)
    private Long batchId;
    private String batchNo;
    private Long skuId;
    private Long warehouseId;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private LocalDate inboundDate;
    private Integer qty;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
