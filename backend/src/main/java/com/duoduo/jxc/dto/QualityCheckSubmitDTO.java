package com.duoduo.jxc.dto;

import lombok.Data;
import java.util.List;

@Data
public class QualityCheckSubmitDTO {
    private Long checkId;
    private String checkNo;
    private Long bundleId;
    private Long processId;
    private String result;
    private Integer checkQuantity;
    private Integer qualifiedQuantity;
    private Integer unqualifiedQuantity;
    private Long checkerId;
    private java.time.LocalDateTime checkTime;
    private String remark;
    private List<DefectRecordDTO> defects;
}
