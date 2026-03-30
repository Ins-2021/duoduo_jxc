package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 款式状态枚举
 */
@Getter
public enum StyleStatusEnum implements DictEnum {
    DRAFT(0, "草稿"),
    ACTIVE(1, "启用"),
    DISABLED(2, "停用"),
    ARCHIVED(3, "归档");

    private final int code;
    private final String label;

    StyleStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
