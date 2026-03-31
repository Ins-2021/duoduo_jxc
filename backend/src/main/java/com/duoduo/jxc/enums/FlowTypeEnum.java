package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 扎包流转类型枚举
 *
 * @author duoduo
 * @since 1.0.0
 */
@Getter
public enum FlowTypeEnum {

    /** 创建 */
    CREATE("CREATE", "创建"),

    /** 交接 */
    TRANSFER("TRANSFER", "交接"),

    /** 扫码 */
    SCAN("SCAN", "扫码"),

    /** 退还 */
    RETURN("RETURN", "退还"),

    /** 完成 */
    COMPLETE("COMPLETE", "完成");

    private final String code;
    private final String desc;

    FlowTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FlowTypeEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (FlowTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
