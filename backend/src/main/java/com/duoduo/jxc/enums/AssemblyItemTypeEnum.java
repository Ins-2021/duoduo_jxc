package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 组装拆卸明细项类型枚举
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Getter
public enum AssemblyItemTypeEnum implements DictEnum {
    COMPONENT(1, "组件"),
    PRODUCT(2, "成品");

    private final int code;
    private final String label;

    AssemblyItemTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
