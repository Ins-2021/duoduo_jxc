package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum SalesOrderStatusEnum implements DictEnum {
    DRAFT(10, "草稿"),
    PENDING_AUDIT(20, "待审核"),
    RUNNING(30, "执行中"),
    FINISHED(40, "已完成"),
    VOIDED(90, "已作废");

    private final int code;
    private final String label;

    SalesOrderStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
