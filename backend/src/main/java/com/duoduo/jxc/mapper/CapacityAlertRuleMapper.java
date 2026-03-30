package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.CapacityAlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产能预警规则Mapper
 */
@Mapper
public interface CapacityAlertRuleMapper extends BaseMapper<CapacityAlertRule> {

    @Select("SELECT * FROM capacity_alert_rule WHERE status = 'active' AND deleted = 0")
    List<CapacityAlertRule> selectActiveRules();
}
