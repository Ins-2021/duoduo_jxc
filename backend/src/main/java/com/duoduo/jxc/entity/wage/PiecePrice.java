package com.duoduo.jxc.entity.wage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 计件工价
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_piece_price")
public class PiecePrice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long priceId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private String processCode;
    private String processName;
    /** 员工ID，null表示标准工价 */
    private Long employeeId;
    private String employeeName;
    /** 计件单价 */
    private BigDecimal unitPrice;
    private String unit;
    /** 工价类型: 0-标准/1-员工/2-批次 */
    private Integer priceType;
    private Long batchId;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    /** 状态: 0-停用/1-启用 */
    private Integer status;
    private String remark;
}
