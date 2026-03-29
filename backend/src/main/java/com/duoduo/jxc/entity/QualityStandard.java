package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_quality_standard")
public class QualityStandard extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long standardId;
    private String standardName;
    private String standardType;
    private String checkItems;
    private String passStandard;
    private Long orderId;
    private Long processId;
    private Integer isActive;
}
