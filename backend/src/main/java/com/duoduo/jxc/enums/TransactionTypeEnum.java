package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum TransactionTypeEnum {
    INCOME(1, "收入"),
    EXPENSE(2, "支出"),
    TRANSFER(3, "转账");

    private final Integer value;
    private final String label;

    TransactionTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (TransactionTypeEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
