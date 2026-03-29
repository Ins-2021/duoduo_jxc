package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_workshop")
public class Workshop extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long workshopId;
    private Long factoryId;
    private String name;
    private String code;
    private String workshopType;
    private Long managerId;
    private Integer isActive;
    private String remark;
}
