package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 工资单状态枚举
 */
@Getter
public enum SalarySheetStatusEnum implements DictEnum {
    DRAFT(0, "草稿"),
    PENDING_AUDIT(1, "待审核"),
    APPROVED(2, "已审核"),
    PAID(3, "已发放"),
    VOIDED(90, "已作废");

    private final int code;
    private final String label;

    SalarySheetStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
