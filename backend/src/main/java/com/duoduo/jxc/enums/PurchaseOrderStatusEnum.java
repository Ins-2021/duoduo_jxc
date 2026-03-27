package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum PurchaseOrderStatusEnum implements DictEnum {
    DRAFT(10, "草稿"),
    RUNNING(20, "执行中"),
    FINISHED(40, "已完成");

    private final int code;
    private final String label;

    PurchaseOrderStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
