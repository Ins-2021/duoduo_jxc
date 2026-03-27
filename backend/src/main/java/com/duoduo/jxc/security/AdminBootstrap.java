package com.duoduo.jxc.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getUsername, "admin")
                .last("limit 1"));
        if (admin == null) {
            return;
        }
        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            sysUserMapper.updateById(admin);
        }
    }
}

