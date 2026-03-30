package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 返工计件关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_rework_piece_work")
public class ReworkPieceWork extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 返工单ID */
    private Long reworkId;
    /** 返工单号 */
    private String reworkNo;
    /** 原始计件记录ID */
    private Long originalRecordId;
    /** 返工计件记录ID */
    private Long reworkRecordId;
    /** 工人ID */
    private Long workerId;
    /** 扎包ID */
    private Long bundleId;
    /** 工序ID */
    private Long processId;
    /** 返工数量 */
    private Integer quantity;
    /** 扣款金额 */
    private BigDecimal deductionAmount;
    /** 返工原因 */
    private String reason;
    /** 状态(pending/confirmed) */
    private String status;
}
