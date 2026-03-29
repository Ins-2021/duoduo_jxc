package com.duoduo.jxc.entity.cost;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cost_center")
public class CostCenter extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long costCenterId;
    private String centerCode;
    private String centerName;
    private Integer centerType;
    private Long parentId;
    private Long managerId;
    private Integer status;
    private String remark;
}
