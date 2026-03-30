package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_record")
public class ProcessRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long recordId;
    /** 记录编号 */
    private String recordNo;
    /** 扎包ID */
    private Long bundleId;
    /** 工序ID */
    private Long processId;
    /** SKU ID（用于溢价计算） */
    private Long skuId;
    /** 主工人ID */
    private Long workerId;
    /** 是否团队协作 */
    private Integer isTeamWork;
    /** 协作类型(main_assistant/custom) */
    private String teamType;
    /** 团队组ID */
    private String teamGroupId;
    /** 计件数量 */
    private Integer quantity;
    /** 价格等级(price1/price2/price3) */
    private String priceLevel;
    /** SPU基准单价（快照） */
    private BigDecimal basePrice;
    /** SKU溢价比例(%) */
    private BigDecimal premiumRatio;
    /** 最终单价（快照） */
    private BigDecimal unitPrice;
    /** 分配比例(%) */
    private BigDecimal shareRatio;
    /** 计件金额 */
    private BigDecimal amount;
    /** 记录类型(normal/rework/deduction/bonus) */
    private String recordType;
    /** 关联返工单ID */
    private Long relatedReworkId;
    /** 扣款原因 */
    private String deductionReason;
    /** 结算状态(unsettled/settled) */
    private String settlementStatus;
    /** 关联工资单ID */
    private Long salarySheetId;
    /** 结算时间 */
    private LocalDateTime settledAt;
    /** 扫码时间 */
    private LocalDateTime scanTime;
    /** 确认时间 */
    private LocalDateTime confirmTime;
    /** 状态(pending/confirmed/rejected) */
    private String status;
    /** 设备ID */
    private String deviceId;
    /** 位置 */
    private String location;
    /** 备注 */
    private String remark;
}
