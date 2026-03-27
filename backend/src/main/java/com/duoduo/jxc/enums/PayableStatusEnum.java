package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum PayableStatusEnum {
    UNPAID(0, "未付款"),
    PARTIAL(1, "部分付款"),
    PAID(2, "已付款");

    private final Integer value;
    private final String label;

    PayableStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (PayableStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
