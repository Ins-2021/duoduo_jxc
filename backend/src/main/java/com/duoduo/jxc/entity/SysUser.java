package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体类
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_user")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private Long deptId;

    private String username;
    private String realName;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;
    
    private Long storeId;
    private String storeName;
    private String remark;
    private String permissions;
    private String dataAuth;
    private Integer status;
}
