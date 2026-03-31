package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 扫码类型枚举
 *
 * @author duoduo
 * @since 1.0.0
 */
@Getter
public enum ScanTypeEnum {

    /** 二维码 */
    QR_CODE("QR_CODE", "二维码"),

    /** 条形码 */
    BAR_CODE("BAR_CODE", "条形码"),

    /** 手动输入 */
    MANUAL("MANUAL", "手动输入");

    private final String code;
    private final String desc;

    ScanTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ScanTypeEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (ScanTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
