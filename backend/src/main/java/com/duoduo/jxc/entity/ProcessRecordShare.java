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
    /** 主记录ID */
    private Long recordId;
    /** 团队组ID */
    private String teamGroupId;
    /** 工人ID */
    private Long workerId;
    /** 角色(leader/member) */
    private String workerRole;
    /** 分配比例(%) */
    private BigDecimal shareRatio;
    /** 分配金额 */
    private BigDecimal shareAmount;
    /** 结算状态(unsettled/settled) */
    private String settlementStatus;
    /** 关联工资单ID */
    private Long salarySheetId;
}
