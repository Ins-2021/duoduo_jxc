package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.rbac.ResetPasswordRequest;
import com.duoduo.jxc.dto.rbac.UserCreateRequest;
import com.duoduo.jxc.dto.rbac.UserRoleAssignRequest;
import com.duoduo.jxc.dto.rbac.UserUpdateRequest;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysUserRole;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.mapper.SysUserRoleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/users")
@RequiredArgsConstructor
public class SystemUserController {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("@perm.has('system:user:view')")
    public Result<List<SysUser>> list(@RequestParam(required = false) String username) {
        return Result.success(sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .like(username != null && !username.isBlank(), SysUser::getUsername, username)
                .orderByDesc(SysUser::getUserId)));
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:user:add')")
    public Result<Long> create(@RequestBody @Valid UserCreateRequest request) {
        SysUser exist = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getUsername, request.getUsername())
                .last("limit 1"));
        if (exist != null) {
            throw new BusinessException(BizCode.USER_ALREADY_EXISTS);
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setDeptId(request.getDeptId());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        sysUserMapper.insert(user);
        return Result.success(user.getUserId());
    }

    @PutMapping("/{userId}")
    @PreAuthorize("@perm.has('system:user:edit')")
    public Result<Void> update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getDeptId() != null) {
            user.setDeptId(request.getDeptId());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@perm.has('system:user:delete')")
    public Result<Void> delete(@PathVariable Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            return Result.success();
        }
        user.setDeleted(1);
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("@perm.has('system:user:resetpwd')")
    public Result<Void> resetPassword(@PathVariable Long userId, @RequestBody @Valid ResetPasswordRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);
        return Result.success();
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("@perm.has('system:user:grant')")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody @Valid UserRoleAssignRequest request) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        for (Long roleId : request.getRoleIds()) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
        return Result.success();
    }
}

