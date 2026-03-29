package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum QuotationStatusEnum {
    DRAFT("draft", "草稿"),
    ACCEPTED("accepted", "已接受"),
    REJECTED("rejected", "已拒绝"),
    CANCELLED("cancelled", "已取消");

    private final String value;
    private final String label;

    QuotationStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(String value) {
        if (value == null) return "";
        for (QuotationStatusEnum e : values()) {
            if (e.value.equals(value)) {
                return e.label;
            }
        }
        return "";
    }
}
