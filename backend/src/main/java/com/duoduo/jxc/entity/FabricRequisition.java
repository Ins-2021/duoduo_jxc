package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_fabric_requisition")
public class FabricRequisition {
    @TableId(type = IdType.AUTO)
    private Long requisitionId;
    private String requisitionNo;
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private Long warehouseId;
    private BigDecimal quantity;
    private Long productionOrderId;
    private Long applicantId;
    private String purpose;
    private Integer status;
    private String approveComment;
    private LocalDateTime approveTime;
    private Long approveBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
