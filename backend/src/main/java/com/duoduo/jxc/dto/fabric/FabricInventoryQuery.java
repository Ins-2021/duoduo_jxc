package com.duoduo.jxc.dto.fabric;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FabricInventoryQuery extends PageQuery {
    private Long fabricId;
    private Long warehouseId;
    private String fabricCode;
    private String fabricName;
}
