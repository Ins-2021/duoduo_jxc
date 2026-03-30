package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_patrol_check")
public class PatrolCheck extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long patrolId;
    private String patrolNo;
    private Long workshopId;
    private Long inspectorId;
    private String checkType;
    private String result;
    private String remark;
    private java.time.LocalDateTime checkTime;
}
