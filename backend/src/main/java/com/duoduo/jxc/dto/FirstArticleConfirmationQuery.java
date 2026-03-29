package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FirstArticleConfirmationQuery extends PageQuery {
    private String confirmationNo;
    private Long orderId;
    private Long processId;
    private String result;
    private Long checkerId;
}
