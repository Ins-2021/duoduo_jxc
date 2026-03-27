package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class TransferOrderDTO {
    private Long transferId;
    private String transferNo;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private String fromWarehouseName;
    private String toWarehouseName;
    private Integer status;
    private LocalDateTime transferDate;
    private String remark;
    private List<TransferOrderDetailDTO> details;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
