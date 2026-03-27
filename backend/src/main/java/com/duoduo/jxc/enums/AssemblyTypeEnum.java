package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum AssemblyTypeEnum {
    ASSEMBLY(1, "组装"),
    DISASSEMBLY(2, "拆卸");

    private final Integer value;
    private final String label;

    AssemblyTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (AssemblyTypeEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
