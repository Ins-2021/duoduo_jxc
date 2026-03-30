package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 延期风险DTO
 */
@Data
public class DelayRiskDTO {

    private Long orderId;
    private String orderNo;
    private String styleNo;
    private String styleName;
    private LocalDate deadline;
    private Integer remainingDays;
    private Integer progress;
    private BigDecimal delayProbability;
    private String riskLevel;
    private String suggestion;
    private List<ProcessBottleneckDTO> bottlenecks;
}
