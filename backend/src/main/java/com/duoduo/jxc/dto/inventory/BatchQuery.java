package com.duoduo.jxc.dto.inventory;

import lombok.Data;

@Data
public class BatchQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long skuId;
    private Long warehouseId;
    private String batchNo;
}
