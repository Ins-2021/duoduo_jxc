package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum PurchaseOrderTypeEnum implements DictEnum {
    PURCHASE(1, "进货单"),
    BOOKING(2, "进货预订单"),
    RETURN(3, "进货退货单");

    private final int code;
    private final String label;

    PurchaseOrderTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
