package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum StockInOutTypeEnum {
    OTHER_IN(1, "其他入库"),
    OTHER_OUT(2, "其他出库");

    private final Integer value;
    private final String label;

    StockInOutTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (StockInOutTypeEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
