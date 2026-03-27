package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.rbac.RoleCreateRequest;
import com.duoduo.jxc.dto.rbac.RoleMenuAssignRequest;
import com.duoduo.jxc.dto.rbac.RoleUpdateRequest;
import com.duoduo.jxc.entity.rbac.SysRole;
import com.duoduo.jxc.entity.rbac.SysRoleMenu;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SysRoleMapper;
import com.duoduo.jxc.mapper.SysRoleMenuMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/roles")
@RequiredArgsConstructor
public class SystemRoleController {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @GetMapping
    @PreAuthorize("@perm.has('system:role:view')")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getDeleted, 0)
                .orderByDesc(SysRole::getRoleId)));
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:role:add')")
    public Result<Long> create(@RequestBody @Valid RoleCreateRequest request) {
        SysRole exist = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getDeleted, 0)
                .eq(SysRole::getRoleKey, request.getRoleKey())
                .last("limit 1"));
        if (exist != null) {
            throw new BusinessException(BizCode.ROLE_KEY_DUPLICATE);
        }
        SysRole role = new SysRole();
        role.setRoleKey(request.getRoleKey());
        role.setRoleName(request.getRoleName());
        role.setDataScope(request.getDataScope() == null ? "1" : request.getDataScope());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        sysRoleMapper.insert(role);
        return Result.success(role.getRoleId());
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("@perm.has('system:role:edit')")
    public Result<Void> update(@PathVariable Long roleId, @RequestBody RoleUpdateRequest request) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || role.getDeleted() != null && role.getDeleted() == 1) {
            throw new BusinessException(BizCode.ROLE_NOT_FOUND);
        }
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getDataScope() != null) {
            role.setDataScope(request.getDataScope());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        sysRoleMapper.updateById(role);
        return Result.success();
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("@perm.has('system:role:delete')")
    public Result<Void> delete(@PathVariable Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || role.getDeleted() != null && role.getDeleted() == 1) {
            return Result.success();
        }
        role.setDeleted(1);
        sysRoleMapper.updateById(role);
        return Result.success();
    }

    @GetMapping("/{roleId}/menus")
    @PreAuthorize("@perm.has('system:role:grant')")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        List<SysRoleMenu> list = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIds = list.stream().map(SysRoleMenu::getMenuId).toList();
        return Result.success(menuIds);
    }

    @PostMapping("/{roleId}/menus")
    @PreAuthorize("@perm.has('system:role:grant')")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody @Valid RoleMenuAssignRequest request) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        for (Long menuId : request.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            sysRoleMenuMapper.insert(rm);
        }
        return Result.success();
    }
}

