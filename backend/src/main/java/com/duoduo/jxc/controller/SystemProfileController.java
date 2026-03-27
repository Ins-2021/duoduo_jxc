package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.rbac.CurrentUserResponse;
import com.duoduo.jxc.dto.rbac.ResetPasswordRequest;
import com.duoduo.jxc.dto.rbac.ChangePasswordRequest;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.rbac.SysRole;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SysRoleMapper;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.service.rbac.SysPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemProfileController {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionService sysPermissionService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public Result<CurrentUserResponse> profile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        List<String> perms = sysPermissionService.getUserPerms(user);
        List<SysRole> roles = sysPermissionService.isAdmin(user) ? List.of() : sysRoleMapper.selectRolesByUserId(userId);

        CurrentUserResponse resp = new CurrentUserResponse();
        resp.setUserId(user.getUserId());
        resp.setUsername(user.getUsername());
        resp.setRealName(user.getRealName());
        resp.setPerms(perms);
        resp.setRoles(roles.stream().map(SysRole::getRoleKey).toList());
        return Result.success(resp);
    }

    @PostMapping("/profile/change-password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(BizCode.OLD_PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);
        return Result.success();
    }
}

