package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum InventoryCheckStatusEnum {
    DRAFT(0, "草稿"),
    CHECKING(1, "盘点中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    private final Integer value;
    private final String label;

    InventoryCheckStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (InventoryCheckStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
