package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 单号规则实体
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
@TableName("jxc_doc_no_rule")
public class DocNoRule {

    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long ruleId;

    /**
     * 单据类型
     */
    private String docType;

    /**
     * 单据名称
     */
    private String docName;

    /**
     * 编号前缀
     */
    private String prefix;

    /**
     * 规则模板（如：XS{Y}{M}{D}{###}）
     */
    private String template;

    /**
     * 流水号位数
     */
    private Integer seqLength;

    /**
     * 是否包含年份（1-是 0-否）
     */
    private Integer includeYear;

    /**
     * 是否包含月份（1-是 0-否）
     */
    private Integer includeMonth;

    /**
     * 是否包含日期（1-是 0-否）
     */
    private Integer includeDay;

    /**
     * 是否启用随机数（1-是 0-否）
     */
    private Integer useRandom;

    /**
     * 随机数长度
     */
    private Integer randomLength;

    /**
     * 状态（1-启用 0-禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
