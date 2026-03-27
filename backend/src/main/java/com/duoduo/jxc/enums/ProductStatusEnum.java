package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum ProductStatusEnum implements DictEnum {
    OFF_SHELF(0, "下架"),
    ON_SHELF(1, "上架");

    private final int code;
    private final String label;

    ProductStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}

