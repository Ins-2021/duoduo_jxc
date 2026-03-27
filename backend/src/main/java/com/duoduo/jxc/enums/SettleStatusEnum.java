package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum SettleStatusEnum implements DictEnum {
    UNSETTLED(0, "未核销"),
    PARTIAL(1, "部分核销"),
    SETTLED(2, "已核销");

    private final int code;
    private final String label;

    SettleStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
