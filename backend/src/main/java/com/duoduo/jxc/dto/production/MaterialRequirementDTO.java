package com.duoduo.jxc.dto.production;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 面料需求计算结果DTO
 */
@Data
public class MaterialRequirementDTO {

    /**
     * 生产订单ID
     */
    private Long productionOrderId;

    /**
     * 生产订单编号
     */
    private String orderNo;

    /**
     * 款式ID
     */
    private Long styleId;

    /**
     * 款式编号
     */
    private String styleNo;

    /**
     * 款式名称
     */
    private String styleName;

    /**
     * 生产数量
     */
    private Integer productionQty;

    /**
     * 面料需求明细列表
     */
    private List<MaterialItemDTO> materials;

    /**
     * 是否有缺口
     */
    private Boolean hasShortage;

    /**
     * 面料需求明细
     */
    @Data
    public static class MaterialItemDTO {
        /**
         * 面料ID
         */
        private Long fabricId;

        /**
         * 面料编码
         */
        private String fabricCode;

        /**
         * 面料名称
         */
        private String fabricName;

        /**
         * 面料类型
         */
        private String fabricType;

        /**
         * 单位
         */
        private String unit;

        /**
         * 单件用量
         */
        private BigDecimal unitUsage;

        /**
         * 损耗率
         */
        private BigDecimal wastageRate;

        /**
         * 需求量（含损耗）
         */
        private BigDecimal requiredQty;

        /**
         * 可用库存
         */
        private BigDecimal availableQty;

        /**
         * 缺口数量
         */
        private BigDecimal shortageQty;

        /**
         * 是否充足
         */
        private Boolean sufficient;
    }
}
