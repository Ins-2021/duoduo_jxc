package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_config")
public class SysConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long configId;

    private String configKey;

    private String configValue;

    private String remark;
}
