package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum TransferOrderStatusEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "已审核"),
    IN_TRANSIT(2, "运输中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer value;
    private final String label;

    TransferOrderStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        if (value == null) return "";
        for (TransferOrderStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
