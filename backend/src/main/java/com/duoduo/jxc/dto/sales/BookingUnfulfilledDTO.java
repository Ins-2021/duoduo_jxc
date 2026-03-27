package com.duoduo.jxc.dto.sales;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 预订单欠货明细DTO
 */
@Data
public class BookingUnfulfilledDTO {

    /**
     * 明细ID
     */
    private Long detailId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 预定数量
     */
    private Integer bookedQty;

    /**
     * 已发货数量
     */
    private Integer deliveryQty;

    /**
     * 未发货数量
     */
    private Integer unfulfilledQty;

    /**
     * 单价
     */
    private BigDecimal unitPrice;
}
