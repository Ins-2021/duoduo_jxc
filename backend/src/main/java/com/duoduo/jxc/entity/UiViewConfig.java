package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_ui_view_config")
public class UiViewConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long configId;

    private String viewKey;

    private String viewName;

    private String scene;

    private String configJson;

    private String defaultJson;

    private Integer version;

    private String remark;
}

