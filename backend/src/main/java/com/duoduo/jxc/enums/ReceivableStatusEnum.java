package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum ReceivableStatusEnum {
    UNPAID(0, "未收款"),
    PARTIAL(1, "部分收款"),
    PAID(2, "已收款");

    private final Integer value;
    private final String label;

    ReceivableStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (ReceivableStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
