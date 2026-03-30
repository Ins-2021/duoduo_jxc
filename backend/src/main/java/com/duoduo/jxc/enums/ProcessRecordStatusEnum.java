package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 计件记录状态枚举
 */
@Getter
public enum ProcessRecordStatusEnum {
    PENDING("pending", "待确认"),
    CONFIRMED("confirmed", "已确认"),
    REJECTED("rejected", "已驳回");

    private final String code;
    private final String label;

    ProcessRecordStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
