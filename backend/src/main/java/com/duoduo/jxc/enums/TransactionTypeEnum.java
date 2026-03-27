package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum TransactionTypeEnum implements DictEnum {
    INCOME(1, "收入"),
    EXPENSE(2, "支出"),
    TRANSFER(3, "转账");

    private final int code;
    private final String label;

    TransactionTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (TransactionTypeEnum e : values()) {
            if (e.code == value) {
                return e.label;
            }
        }
        return "";
    }
}
