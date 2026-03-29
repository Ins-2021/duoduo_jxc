package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bom")
public class Bom extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long bomId;
    private String bomNo;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private String versionNo;
    private Integer status;
    private LocalDate effectiveDate;
    private String remark;
}
