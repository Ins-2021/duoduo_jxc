package com.duoduo.jxc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 进销存系统后端启动类
 *
 * @author zxh
 * @since 2026-03-23
 */
@SpringBootApplication
@MapperScan("com.duoduo.jxc.mapper")
@EnableTransactionManagement
public class JxcApplication {
    public static void main(String[] args) {
        SpringApplication.run(JxcApplication.class, args);
    }
}
