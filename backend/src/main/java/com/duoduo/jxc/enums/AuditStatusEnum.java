package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 审核状态枚举
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Getter
public enum AuditStatusEnum implements DictEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已驳回");

    private final int code;
    private final String label;

    AuditStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
