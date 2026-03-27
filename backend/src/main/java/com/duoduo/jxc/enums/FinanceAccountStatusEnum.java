package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum FinanceAccountStatusEnum implements DictEnum {
    DISABLED(0, "停用"),
    NORMAL(1, "正常");

    private final int code;
    private final String label;

    FinanceAccountStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}

