package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 订单优先级枚举
 */
@Getter
public enum OrderPriorityEnum implements DictEnum {
    LOW(1, "低"),
    NORMAL(2, "普通"),
    HIGH(3, "高"),
    URGENT(4, "紧急");

    private final int code;
    private final String label;

    OrderPriorityEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
