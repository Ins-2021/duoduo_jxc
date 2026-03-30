package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.BundleProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BundleProcessMapper extends BaseMapper<BundleProcess> {

    @Select("SELECT * FROM jxc_bundle_process WHERE bundle_id = #{bundleId}")
    List<BundleProcess> selectByBundleId(@Param("bundleId") Long bundleId);
}
