package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_quality_check")
public class QualityCheck extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long checkId;
    private String checkNo;
    private Long bundleId;
    private Long processId;
    @TableField("check_result")
    private String result;
    private Integer checkQuantity;
    private Integer qualifiedQuantity;
    @TableField("defective_quantity")
    private Integer defectiveQuantity;
    @TableField("inspector_id")
    private Long inspectorId;
    private java.time.LocalDateTime checkTime;
    private String remark;
}
