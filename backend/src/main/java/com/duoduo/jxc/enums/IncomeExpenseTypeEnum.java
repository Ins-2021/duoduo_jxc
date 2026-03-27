package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum IncomeExpenseTypeEnum {
    INCOME(1, "收入"),
    EXPENSE(2, "支出");

    private final Integer value;
    private final String label;

    IncomeExpenseTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (IncomeExpenseTypeEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
