package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum OutStockStatusEnum implements DictEnum {
    NOT_OUT(0, "未出库"),
    PARTIAL(1, "部分出库"),
    DONE(2, "已出库");

    private final int code;
    private final String label;

    OutStockStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
