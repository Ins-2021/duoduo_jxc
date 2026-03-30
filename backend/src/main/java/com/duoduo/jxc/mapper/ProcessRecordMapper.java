package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.ProcessRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProcessRecordMapper extends BaseMapper<ProcessRecord> {

    @Select("SELECT DISTINCT process_id FROM jxc_process_record WHERE bundle_id = #{bundleId} AND status IN ('pending', 'approved') AND deleted = 0")
    List<Long> selectCompletedProcesses(@Param("bundleId") Long bundleId);
}
