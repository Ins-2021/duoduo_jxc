package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商对账单
 */
@Data
@TableName("jxc_supplier_reconciliation")
public class SupplierReconciliation {
    @TableId(type = IdType.AUTO)
    private Long reconciliationId;
    /** 对账单号 */
    private String reconNo;
    /** 供应商ID */
    private Long supplierId;
    /** 供应商名称 */
    private String supplierName;
    /** 对账开始日期 */
    private LocalDate startDate;
    /** 对账结束日期 */
    private LocalDate endDate;
    /** 应付金额 */
    private BigDecimal totalAmount;
    /** 已付金额 */
    private BigDecimal paidAmount;
    /** 未付金额 */
    private BigDecimal unpaidAmount;
    /** 对账状态 0-未对账 1-已对账 2-已确认 */
    private Integer status;
    /** 备注 */
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
