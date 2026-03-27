package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sales_order")
public class SalesOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单 ID
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 单据编号 (业务生成)
     */
    private String docNo;

    /**
     * 业务类型 (1:批发单, 2:零售单, 3:销售预订单)
     */
    private Integer orderType;

    /**
     * 业务日期
     */
    private LocalDate docDate;

    /**
     * 所属门店 ID
     */
    private Long storeId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 制单人 ID
     */
    private Long operatorId;

    /**
     * 状态 (10:草稿, 20:待审核, 30:执行中/部分发货, 40:已完成, 90:已作废)
     */
    private Integer status;

    /**
     * 财务核销状态 (0:未核销, 1:部分核销, 2:已核销)
     */
    private Integer settleStatus;

    /**
     * 折前总额 (明细行金额之和)
     */
    private BigDecimal totalAmount;

    /**
     * 整体折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 其它费用 (如：运费)
     */
    private BigDecimal otherFee;

    /**
     * 实际应收总计 = 折前总额 - 整体折扣金额 + 其它费用
     */
    private BigDecimal actualAmount;

    /**
     * 已收金额
     */
    private BigDecimal paidAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人 ID
     */
    private Long auditBy;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
}
