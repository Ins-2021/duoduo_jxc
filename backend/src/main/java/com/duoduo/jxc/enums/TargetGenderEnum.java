package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 目标性别枚举
 */
@Getter
public enum TargetGenderEnum {
    MALE("male", "男"),
    FEMALE("female", "女"),
    UNISEX("unisex", "男女通用");

    private final String code;
    private final String label;

    TargetGenderEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
