package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductionEfficiencyDTO {
    private LocalDate reportDate;
    private Integer planQuantity;
    private Integer actualQuantity;
    private BigDecimal completionRate;
    private Integer qualifiedQuantity;
    private BigDecimal qualityRate;
    private Integer workerCount;
    private BigDecimal avgOutput;
}
