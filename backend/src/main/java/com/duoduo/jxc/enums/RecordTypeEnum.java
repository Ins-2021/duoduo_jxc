package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 计件类型枚举
 */
@Getter
public enum RecordTypeEnum {
    NORMAL("normal", "正常计件"),
    REWORK("rework", "返工计件"),
    SUPPLEMENT("supplement", "补件");

    private final String code;
    private final String label;

    RecordTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
