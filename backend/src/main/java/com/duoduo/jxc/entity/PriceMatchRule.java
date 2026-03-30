package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 工价匹配规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_price_match_rule")
public class PriceMatchRule extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 规则名称 */
    private String ruleName;
    /** 工序ID */
    private Long processId;
    /** 技能等级(trainee/junior/medium/senior/master) */
    private String skillLevel;
    /** 时段(daytime/night/overtime) */
    private String timePeriod;
    /** 基础单价 */
    private BigDecimal basePrice;
    /** 是否启用 */
    private Integer enabled;
    /** 排序 */
    private Integer sortOrder;
    /** 备注 */
    private String remark;
}
