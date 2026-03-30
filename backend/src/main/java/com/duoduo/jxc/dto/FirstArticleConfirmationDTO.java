package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class FirstArticleConfirmationDTO {
    private Long confirmationId;
    private String confirmationNo;
    private Long orderId;
    private Long processId;
    private String status;
    private String result;
    private Long checkerId;
    private Long approverId;
    private String approveComment;
    private java.time.LocalDateTime approveTime;
    private java.time.LocalDateTime checkTime;
    private String remark;
}
