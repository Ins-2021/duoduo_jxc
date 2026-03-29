package com.duoduo.jxc.dto.bom;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BomProcessDTO {
    private Long id;
    private Long bomId;
    private Long processId;
    private String processCode;
    private String processName;
    private Integer sequence;
    private BigDecimal standardTime;
    private BigDecimal piecePrice;
    private LocalDateTime createTime;
}
