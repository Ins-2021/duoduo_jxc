package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum AssemblyOrderStatusEnum {
    DRAFT(0, "草稿"),
    APPROVED(1, "已审核");

    private final Integer value;
    private final String label;

    AssemblyOrderStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (AssemblyOrderStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
