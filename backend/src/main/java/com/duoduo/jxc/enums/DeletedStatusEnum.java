package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 删除状态枚举
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Getter
public enum DeletedStatusEnum implements DictEnum {
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final int code;
    private final String label;

    DeletedStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
