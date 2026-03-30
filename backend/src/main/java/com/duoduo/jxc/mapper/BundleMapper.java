package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.Bundle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BundleMapper extends BaseMapper<Bundle> {

    @Select("SELECT * FROM jxc_bundle WHERE bundle_no = #{bundleNo} AND deleted = 0")
    Bundle selectByBundleNo(@Param("bundleNo") String bundleNo);
}
