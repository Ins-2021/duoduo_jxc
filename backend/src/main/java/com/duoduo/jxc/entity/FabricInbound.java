package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_fabric_inbound")
public class FabricInbound {
    @TableId(type = IdType.AUTO)
    private Long inboundId;
    private String inboundNo;
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private String batchNo;
    private Long supplierId;
    private Integer status;
    private String remark;
    private LocalDateTime inboundDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
