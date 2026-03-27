package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum CommonStatusEnum implements DictEnum {
    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    private final int code;
    private final String label;

    CommonStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
