package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 首件确认结果枚举
 */
@Getter
public enum FirstArticleResultEnum {
    PASSED("passed", "通过"),
    FAILED("failed", "未通过"),
    PENDING("pending", "待确认");

    private final String code;
    private final String label;

    FirstArticleResultEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
