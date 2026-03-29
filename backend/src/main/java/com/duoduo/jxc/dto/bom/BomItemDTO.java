package com.duoduo.jxc.dto.bom;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BomItemDTO {
    private Long itemId;
    private Long bomId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String color;
    private String size;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal wastageRate;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
