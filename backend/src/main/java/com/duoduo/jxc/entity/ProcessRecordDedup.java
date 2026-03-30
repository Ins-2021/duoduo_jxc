package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计件记录防重表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_record_dedup")
public class ProcessRecordDedup extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 防重键(如: workerId_processId_bundleId_skuId) */
    private String dedupKey;
    /** 工人ID */
    private Long workerId;
    /** 工序ID */
    private Long processId;
    /** 扎包ID */
    private Long bundleId;
    /** SKU ID */
    private Long skuId;
    /** 扫码时间 */
    private java.time.LocalDateTime scanTime;
    /** 设备ID */
    private String deviceId;
}
