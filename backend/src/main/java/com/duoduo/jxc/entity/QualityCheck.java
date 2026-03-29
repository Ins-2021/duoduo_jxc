package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private String result;
    private Integer checkQuantity;
    private Integer qualifiedQuantity;
    private Integer unqualifiedQuantity;
    private Long checkerId;
    private java.time.LocalDateTime checkTime;
    private String remark;
}
