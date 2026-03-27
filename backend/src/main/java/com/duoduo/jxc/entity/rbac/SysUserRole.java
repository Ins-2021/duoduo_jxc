package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("jxc_sys_user_role")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;

    private LocalDateTime createTime;
}

