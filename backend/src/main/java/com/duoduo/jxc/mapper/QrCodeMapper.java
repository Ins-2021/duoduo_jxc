package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.QrCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QrCodeMapper extends BaseMapper<QrCode> {

    @Select("SELECT * FROM jxc_qr_code WHERE qr_code_no = #{qrCodeNo} AND deleted = 0")
    QrCode selectByQrCodeNo(@Param("qrCodeNo") String qrCodeNo);

    @Select("SELECT * FROM jxc_qr_code WHERE ref_type = #{refType} AND ref_id = #{refId} AND deleted = 0")
    QrCode selectByRef(@Param("refType") String refType, @Param("refId") Long refId);

    @Update("UPDATE jxc_qr_code SET scan_count = scan_count + 1, last_scan_at = NOW() " +
            "WHERE qr_id = #{qrId}")
    int incrementScanCount(@Param("qrId") Long qrId);

    @Update("UPDATE jxc_qr_code SET print_count = print_count + 1, printed = 1, last_print_at = NOW() " +
            "WHERE qr_id = #{qrId}")
    int incrementPrintCount(@Param("qrId") Long qrId);
}
