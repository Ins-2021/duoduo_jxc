package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class InventoryCheckDTO {
    private Long checkId;
    private String checkNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private LocalDateTime checkDate;
    private String remark;
    private List<InventoryCheckDetailDTO> details;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
