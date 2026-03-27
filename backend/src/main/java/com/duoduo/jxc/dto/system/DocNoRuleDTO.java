package com.duoduo.jxc.dto.system;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 单号规则DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class DocNoRuleDTO {
    /**
     * 规则ID
     */
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
     * 规则模板
     */
    private String template;

    /**
     * 流水号位数
     */
    private Integer seqLength;

    /**
     * 是否包含年份
     */
    private Integer includeYear;

    /**
     * 是否包含月份
     */
    private Integer includeMonth;

    /**
     * 是否包含日期
     */
    private Integer includeDay;

    /**
     * 是否启用随机数
     */
    private Integer useRandom;

    /**
     * 随机数长度
     */
    private Integer randomLength;

    /**
     * 状态
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
