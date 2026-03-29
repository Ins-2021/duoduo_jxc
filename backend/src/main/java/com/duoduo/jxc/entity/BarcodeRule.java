package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 条码生成规则
 */
@Data
@TableName("jxc_barcode_rule")
public class BarcodeRule {
    @TableId(type = IdType.AUTO)
    private Long ruleId;
    private String ruleName;
    /** 类型: SKU/BOX/BATCH */
    private String ruleType;
    private String prefix;
    /** 日期格式, 如 yyyyMMdd */
    private String dateFormat;
    /** 序列号长度, 默认4 */
    private Integer seqLength;
    /** 规则表达式, 如 {prefix}{date}{seq} */
    private String ruleExpression;
    /** 示例 */
    private String example;
    /** 是否默认规则 0-否 1-是 */
    private Integer isDefault;
    /** 是否启用 0-禁用 1-启用 */
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
