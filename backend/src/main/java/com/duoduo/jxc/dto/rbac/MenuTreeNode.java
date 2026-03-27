package com.duoduo.jxc.dto.rbac;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTreeNode {

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

    private List<MenuTreeNode> children = new ArrayList<>();
}

