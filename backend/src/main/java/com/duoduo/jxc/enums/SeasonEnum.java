package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 季节枚举
 */
@Getter
public enum SeasonEnum {
    SPRING("spring", "春"),
    SUMMER("summer", "夏"),
    AUTUMN("autumn", "秋"),
    WINTER("winter", "冬"),
    ALL_YEAR("all_year", "四季");

    private final String code;
    private final String label;

    SeasonEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
