package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 条码记录
 */
@Data
@TableName("jxc_barcode")
public class Barcode {
    @TableId(type = IdType.AUTO)
    private Long barcodeId;
    /** 条码内容 */
    private String barcodeContent;
    /** 关联类型: SKU/BATCH/BOX */
    private String refType;
    /** 关联ID */
    private Long refId;
    /** 生成规则ID */
    private Long ruleId;
    /** 条码类型: SKU/BOX/BATCH */
    private String barcodeType;
    /** 是否已打印 0-否 1-是 */
    private Integer printed;
    /** 打印时间 */
    private LocalDateTime printTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
