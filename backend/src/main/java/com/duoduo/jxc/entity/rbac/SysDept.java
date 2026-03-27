package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_dept")
public class SysDept extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long deptId;
    private Long parentId;
    private String ancestors;
    private String deptName;
    private Integer orderNum;
    private String leader;
    private String phone;
    private String email;
    private Integer status;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private java.util.List<SysDept> children = new java.util.ArrayList<>();
}