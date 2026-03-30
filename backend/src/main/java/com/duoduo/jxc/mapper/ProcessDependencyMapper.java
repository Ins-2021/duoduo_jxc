package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.ProcessDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProcessDependencyMapper extends BaseMapper<ProcessDependency> {

    @Select("SELECT * FROM jxc_process_dependency WHERE process_id = #{processId} ORDER BY sort_order")
    List<ProcessDependency> selectByProcessId(@Param("processId") Long processId);
}
