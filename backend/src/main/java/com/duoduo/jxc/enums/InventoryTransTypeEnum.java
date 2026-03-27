package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 库存流水类型枚举
 */
@Getter
public enum InventoryTransTypeEnum implements DictEnum {
    STOCK_IN(1, "入库"),
    STOCK_OUT(2, "出库"),
    LOCK(3, "锁定"),
    UNLOCK(4, "解锁"),
    ADJUST(5, "盘点调整");

    private final int code;
    private final String label;

    InventoryTransTypeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
