package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class StockInOutDTO {
    private Long inOutId;
    private String billNo;
    private Integer type;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private LocalDateTime billDate;
    private String remark;
    private List<StockInOutDetailDTO> details;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
