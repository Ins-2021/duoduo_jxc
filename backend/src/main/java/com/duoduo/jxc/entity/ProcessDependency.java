package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_dependency")
public class ProcessDependency extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long dependencyId;
    private Long processId;
    private Long preProcessId;
    private Integer sortOrder;
}
