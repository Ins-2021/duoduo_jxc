package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 时段类型枚举
 */
@Getter
public enum TimePeriodEnum {
    DAYTIME("daytime", "白天"),
    NIGHT("night", "夜班"),
    OVERTIME("overtime", "加班");

    private final String code;
    private final String label;

    TimePeriodEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
