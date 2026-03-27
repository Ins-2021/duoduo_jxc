package com.duoduo.jxc.dto.printtemplate;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintTemplateQuery extends PageQuery {
    private String templateName;
    private String bizType;
    private Boolean showDisabled;
}

