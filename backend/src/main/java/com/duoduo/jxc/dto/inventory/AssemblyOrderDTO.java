package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class AssemblyOrderDTO {
    private Long assemblyId;
    private String assemblyNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer type;
    private Integer status;
    private LocalDateTime assemblyDate;
    private String remark;
    private List<AssemblyOrderDetailDTO> details;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
