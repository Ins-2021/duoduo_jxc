package com.duoduo.jxc.dto.print;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrintDataDTO {
    private String billNo;
    private String billDate;
    private String storeName;
    private String customerName;
    private String operator;
    private String remark;
    private BigDecimal totalAmount;
    private List<PrintDataItemDTO> items;
}

