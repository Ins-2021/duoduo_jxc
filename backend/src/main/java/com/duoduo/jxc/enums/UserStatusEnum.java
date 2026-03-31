package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Getter
public enum UserStatusEnum implements DictEnum {
    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    private final int code;
    private final String label;

    UserStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
