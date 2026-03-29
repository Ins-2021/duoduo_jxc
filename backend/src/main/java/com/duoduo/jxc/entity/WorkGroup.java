package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_work_group")
public class WorkGroup extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long groupId;
    private String name;
    private String code;
    private Long factoryId;
    private Long workshopId;
    private Long leaderId;
    private String groupType;
    private Integer isActive;
    private String remark;
}
