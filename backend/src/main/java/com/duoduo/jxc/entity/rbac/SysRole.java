package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_role")
public class SysRole extends BaseEntity {

    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    private String roleKey;
    private String roleName;
    private String dataScope;
    private Integer status;
    private String remark;
}

