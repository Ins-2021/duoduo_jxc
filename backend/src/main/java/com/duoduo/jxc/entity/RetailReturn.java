package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("jxc_retail_return")
public class RetailReturn {
    @TableId(type = IdType.AUTO)
    private Long returnId;
    private String docNo;
    private Long customerId;
    private String customerName;
    private LocalDate returnDate;
    private BigDecimal totalAmount;
    private Integer status;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private Long auditBy;
    private String auditByName;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
