package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 扫码记录状态枚举
 *
 * @author duoduo
 * @since 1.0.0
 */
@Getter
public enum RecordStatusEnum {

    /** 待确认 */
    PENDING("PENDING", "待确认"),

    /** 已确认 */
    CONFIRMED("CONFIRMED", "已确认"),

    /** 已拒绝 */
    REJECTED("REJECTED", "已拒绝");

    private final String code;
    private final String desc;

    RecordStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RecordStatusEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (RecordStatusEnum status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
