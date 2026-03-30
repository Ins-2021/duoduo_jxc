package com.duoduo.jxc.dto.print;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrintDataItemDTO {
    private String name;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal amount;
    private String attr1;
    private String attr2;
}

