package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.rbac.MenuSaveRequest;
import com.duoduo.jxc.dto.rbac.MenuTreeNode;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysMenu;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SysMenuMapper;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.service.rbac.MenuTreeBuilder;
import com.duoduo.jxc.service.rbac.SysPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menus")
@RequiredArgsConstructor
public class SystemMenuController {

    private final SysMenuMapper sysMenuMapper;
    private final SysUserMapper sysUserMapper;
    private final SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    @PreAuthorize("@perm.has('system:menu:view')")
    public Result<List<MenuTreeNode>> tree() {
        List<SysMenu> menus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
        return Result.success(MenuTreeBuilder.build(menus));
    }

    @GetMapping("/routers")
    public Result<List<MenuTreeNode>> routers() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        SysUser user = sysUserMapper.selectById(userId);
        List<SysMenu> menus = sysPermissionService.getUserMenus(user);
        return Result.success(MenuTreeBuilder.build(menus));
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:menu:add')")
    public Result<Long> create(@RequestBody @Valid MenuSaveRequest request) {
        SysMenu menu = new SysMenu();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setOrderNum(request.getOrderNum() == null ? 0 : request.getOrderNum());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setRouteName(request.getRouteName());
        menu.setIcon(request.getIcon());
        menu.setMenuType(request.getMenuType());
        menu.setVisible(request.getVisible() == null ? 1 : request.getVisible());
        menu.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        menu.setPerms(request.getPerms());
        sysMenuMapper.insert(menu);
        return Result.success(menu.getMenuId());
    }

    @PutMapping("/{menuId}")
    @PreAuthorize("@perm.has('system:menu:edit')")
    public Result<Void> update(@PathVariable Long menuId, @RequestBody @Valid MenuSaveRequest request) {
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null || menu.getDeleted() != null && menu.getDeleted() == 1) {
            throw new BusinessException(BizCode.MENU_NOT_FOUND);
        }
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setOrderNum(request.getOrderNum() == null ? 0 : request.getOrderNum());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setRouteName(request.getRouteName());
        menu.setIcon(request.getIcon());
        menu.setMenuType(request.getMenuType());
        menu.setVisible(request.getVisible() == null ? 1 : request.getVisible());
        menu.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        menu.setPerms(request.getPerms());
        sysMenuMapper.updateById(menu);
        return Result.success();
    }

    @DeleteMapping("/{menuId}")
    @PreAuthorize("@perm.has('system:menu:delete')")
    public Result<Void> delete(@PathVariable Long menuId) {
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null || menu.getDeleted() != null && menu.getDeleted() == 1) {
            return Result.success();
        }
        menu.setDeleted(1);
        sysMenuMapper.updateById(menu);
        return Result.success();
    }
}

