package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField(exist = false)
    private String styleCode;
    @TableField(exist = false)
    private String styleName;
    @TableField("version")
    private String versionNo;
    private Integer status;
    @TableField(exist = false)
    private LocalDate effectiveDate;
    @TableField(exist = false)
    private String remark;
}
