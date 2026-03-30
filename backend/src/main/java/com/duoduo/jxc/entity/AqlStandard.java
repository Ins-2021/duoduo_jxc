package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_aql_standard")
public class AqlStandard extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long aqlId;
    private String level;
    private Integer batchSizeMin;
    private Integer batchSizeMax;
    private Integer sampleSize;
    private Integer acceptNum;
    private Integer rejectNum;
}
