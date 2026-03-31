package com.duoduo.jxc.service.rbac.impl;

import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysMenu;
import com.duoduo.jxc.mapper.SysMenuMapper;
import com.duoduo.jxc.service.rbac.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public boolean isAdmin(SysUser user) {
        return user != null && "admin".equalsIgnoreCase(user.getUsername());
    }

    @Override
    public List<String> getUserPerms(SysUser user) {
        if (user == null) {
            return List.of();
        }
        if (isAdmin(user)) {
            List<SysMenu> allMenus = sysMenuMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getDeleted, 0)
                    .eq(SysMenu::getStatus, 1)
                    .isNotNull(SysMenu::getPerms)
                    .ne(SysMenu::getPerms, ""));
            return allMenus.stream().map(SysMenu::getPerms).distinct().toList();
        }
        return sysMenuMapper.selectPermsByUserId(user.getUserId());
    }

    @Override
    public List<SysMenu> getUserMenus(SysUser user) {
        if (user == null) {
            return List.of();
        }
        if (isAdmin(user)) {
            return sysMenuMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getDeleted, 0)
                    .eq(SysMenu::getStatus, 1)
                    .eq(SysMenu::getVisible, 1)
                    .orderByAsc(SysMenu::getParentId)
                    .orderByAsc(SysMenu::getOrderNum));
        }
        return sysMenuMapper.selectMenusByUserId(user.getUserId());
    }
}
