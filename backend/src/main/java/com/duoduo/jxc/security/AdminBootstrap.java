package com.duoduo.jxc.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 管理员账号初始化引导类
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Slf4j
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
            // 从环境变量读取默认密码，如未设置则生成随机密码
            String defaultPassword = System.getenv("ADMIN_DEFAULT_PASSWORD");
            if (defaultPassword == null || defaultPassword.isBlank()) {
                defaultPassword = generateRandomPassword();
                log.warn("=================================================================");
                log.warn("【安全警告】管理员密码未设置，已生成随机密码:");
                log.warn("用户名: admin");
                log.warn("密码: {}", defaultPassword);
                log.warn("请立即登录并修改密码！");
                log.warn("可通过设置 ADMIN_DEFAULT_PASSWORD 环境变量指定初始密码");
                log.warn("=================================================================");
            }
            admin.setPassword(passwordEncoder.encode(defaultPassword));
            sysUserMapper.updateById(admin);
        }
    }

    /**
     * 生成随机密码
     *
     * @return 随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

