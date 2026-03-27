package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum SalesOrderTypeEnum implements DictEnum {
    WHOLESALE(1, "批发单"),
    RETAIL(2, "零售单"),
    BOOKING(3, "销售预订单");

    private final int code;
    private final String label;

    SalesOrderTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
