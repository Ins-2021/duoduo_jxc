package com.duoduo.jxc.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.enums.DeletedStatusEnum;
import com.duoduo.jxc.enums.UserStatusEnum;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.service.rbac.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysPermissionService sysPermissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, DeletedStatusEnum.NOT_DELETED.getCode())
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() != UserStatusEnum.ENABLED.getCode()) {
            throw new com.duoduo.jxc.exception.BusinessException(com.duoduo.jxc.common.BizCode.USER_DISABLED);
        }
        List<String> perms = sysPermissionService.getUserPerms(user);
        List<? extends GrantedAuthority> authorities = perms.stream()
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return User.withUsername(user.getUsername())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
