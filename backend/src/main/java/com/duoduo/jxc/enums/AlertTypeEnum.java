package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum AlertTypeEnum {
    LOW_STOCK(1, "库存不足"),
    HIGH_STOCK(2, "库存过多");

    private final Integer value;
    private final String label;

    AlertTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (AlertTypeEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
