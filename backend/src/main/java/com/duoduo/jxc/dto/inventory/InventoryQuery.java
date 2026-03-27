package com.duoduo.jxc.dto.inventory;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryQuery extends PageQuery {
    private Long warehouseId;
    private Long skuId;
}
