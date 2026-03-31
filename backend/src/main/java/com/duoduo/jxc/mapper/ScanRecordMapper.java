package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.ScanRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScanRecordMapper extends BaseMapper<ScanRecord> {

    @Select("SELECT COUNT(*) FROM jxc_scan_record " +
            "WHERE bundle_id = #{bundleId} AND worker_id = #{workerId} " +
            "AND process_id = #{processId} AND DATE(scan_at) = DATE(#{scanAt}) " +
            "AND deleted = 0")
    int countByBundleWorkerProcessDate(@Param("bundleId") Long bundleId,
                                       @Param("workerId") Long workerId,
                                       @Param("processId") Long processId,
                                       @Param("scanAt") LocalDateTime scanAt);

    @Select("SELECT * FROM jxc_scan_record " +
            "WHERE worker_id = #{workerId} AND DATE(scan_at) = DATE(#{scanAt}) " +
            "AND deleted = 0 ORDER BY scan_at DESC")
    List<ScanRecord> selectByWorkerAndDate(@Param("workerId") Long workerId,
                                           @Param("scanAt") LocalDateTime scanAt);
}
