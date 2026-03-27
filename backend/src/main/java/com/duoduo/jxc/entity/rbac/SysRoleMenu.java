package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("jxc_sys_role_menu")
public class SysRoleMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long menuId;

    private LocalDateTime createTime;
}

