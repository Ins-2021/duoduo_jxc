package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bom_process")
public class BomProcess extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bomId;
    private Long processId;
    private String processCode;
    private String processName;
    private Integer sequence;
    private BigDecimal standardTime;
    private BigDecimal piecePrice;
}
