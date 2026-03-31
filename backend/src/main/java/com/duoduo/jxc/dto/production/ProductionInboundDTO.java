package com.duoduo.jxc.dto.production;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 生产入库请求DTO
 */
@Data
public class ProductionInboundDTO {

    /**
     * 生产订单ID
     */
    @NotNull(message = "生产订单ID不能为空")
    private Long productionOrderId;

    /**
     * 入库仓库ID
     */
    @NotNull(message = "入库仓库不能为空")
    private Long warehouseId;

    /**
     * 入库明细列表
     */
    @NotNull(message = "入库明细不能为空")
    private List<InboundItemDTO> items;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库明细
     */
    @Data
    public static class InboundItemDTO {

        /**
         * SKU ID
         */
        @NotNull(message = "SKU不能为空")
        private Long skuId;

        /**
         * 入库数量
         */
        @NotNull(message = "入库数量不能为空")
        @Min(value = 1, message = "入库数量必须大于0")
        private Integer quantity;
    }
}
