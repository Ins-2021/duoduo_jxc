package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_menu")
public class SysMenu extends BaseEntity {

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private Long parentId;

    private String menuName;

    private Integer orderNum;

    private String path;

    private String component;

    private String routeName;

    private String icon;

    private String menuType;

    private Integer visible;

    private Integer status;

    private String perms;
}

