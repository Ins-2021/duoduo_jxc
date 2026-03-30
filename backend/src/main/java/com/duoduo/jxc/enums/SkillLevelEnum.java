package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 技能等级枚举
 */
@Getter
public enum SkillLevelEnum {
    TRAINEE("trainee", "学徒"),
    JUNIOR("junior", "初级"),
    MEDIUM("medium", "中级"),
    SENIOR("senior", "高级"),
    MASTER("master", "师傅");

    private final String code;
    private final String label;

    SkillLevelEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
