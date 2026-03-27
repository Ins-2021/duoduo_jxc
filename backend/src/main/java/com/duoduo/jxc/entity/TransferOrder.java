package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 调拨单实体
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
@TableName("jxc_transfer_order")
public class TransferOrder {

    /**
     * 调拨单ID
     */
    @TableId(type = IdType.AUTO)
    private Long transferId;

    /**
     * 调拨单号
     */
    private String transferNo;

    /**
     * 调出仓库ID
     */
    private Long fromWarehouseId;

    /**
     * 调入仓库ID
     */
    private Long toWarehouseId;

    /**
     * 调出仓库名称
     */
    private String fromWarehouseName;

    /**
     * 调入仓库名称
     */
    private String toWarehouseName;

    /**
     * 状态 0-待审核 1-已审核 2-运输中 3-已完成 4-已取消
     */
    private Integer status;

    /**
     * 调拨日期
     */
    private LocalDateTime transferDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
