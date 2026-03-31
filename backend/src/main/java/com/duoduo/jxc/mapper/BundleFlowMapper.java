package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.BundleFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BundleFlowMapper extends BaseMapper<BundleFlow> {

    @Select("SELECT * FROM jxc_bundle_flow WHERE bundle_id = #{bundleId} AND deleted = 0 ORDER BY flow_time DESC")
    List<BundleFlow> selectByBundleId(@Param("bundleId") Long bundleId);

    @Select("SELECT * FROM jxc_bundle_flow WHERE bundle_id = #{bundleId} AND deleted = 0 ORDER BY flow_time DESC LIMIT 1")
    BundleFlow selectLatestByBundleId(@Param("bundleId") Long bundleId);
}
