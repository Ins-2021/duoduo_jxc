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
 * 标准成本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_standard_cost")
public class StandardCost {

    @TableId(type = IdType.AUTO)
    private Long costId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal overheadCost;
    private BigDecimal totalCost;
    private BigDecimal materialUsage;
    private BigDecimal materialPrice;
    private BigDecimal laborHours;
    private BigDecimal laborRate;
    private BigDecimal overheadRate;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private String versionNo;
    /** 是否当前版本: 0=no, 1=yes */
    private Integer isCurrent;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
