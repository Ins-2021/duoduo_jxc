package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 质检结果枚举
 */
@Getter
public enum QualityCheckResultEnum {
    PASSED("passed", "合格"),
    FAILED("failed", "不合格"),
    CONDITIONAL("conditional", "条件合格");

    private final String code;
    private final String label;

    QualityCheckResultEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
