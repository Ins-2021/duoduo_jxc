package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.CapacityAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产能预警Mapper
 */
@Mapper
public interface CapacityAlertMapper extends BaseMapper<CapacityAlert> {

    @Select("SELECT * FROM jxc_capacity_alert WHERE status = 'active' AND deleted = 0 ORDER BY create_time DESC")
    List<CapacityAlert> selectActiveAlerts();

    @Select("SELECT * FROM jxc_capacity_alert WHERE factory_id = #{factoryId} AND status = 'active' AND deleted = 0 ORDER BY create_time DESC")
    List<CapacityAlert> selectActiveAlertsByFactory(@Param("factoryId") Long factoryId);
}
