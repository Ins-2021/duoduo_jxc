package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 扎包状态枚举
 */
@Getter
public enum BundleStatusEnum {
    PENDING("pending", "待开始"),
    IN_PROGRESS("in_progress", "进行中"),
    COMPLETED("completed", "已完成"),
    SUSPENDED("suspended", "暂停"),
    SKIPPED("skipped", "跳过");

    private final String code;
    private final String label;

    BundleStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
