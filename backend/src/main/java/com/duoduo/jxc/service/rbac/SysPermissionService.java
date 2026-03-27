package com.duoduo.jxc.service.rbac;

import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysMenu;

import java.util.List;

public interface SysPermissionService {

    boolean isAdmin(SysUser user);

    List<String> getUserPerms(SysUser user);

    List<SysMenu> getUserMenus(SysUser user);
}

