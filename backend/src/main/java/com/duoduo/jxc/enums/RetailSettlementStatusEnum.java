package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 日结单状态枚举
 */
@Getter
public enum RetailSettlementStatusEnum implements DictEnum {
    NORMAL(1, "正常"),
    CANCELLED(0, "作废");

    private final int code;
    private final String label;

    RetailSettlementStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
