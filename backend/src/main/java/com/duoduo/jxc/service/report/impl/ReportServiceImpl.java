package com.duoduo.jxc.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.report.SalesDailyReportDTO;
import com.duoduo.jxc.dto.report.SalesReportQuery;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SalesOrderMapper salesOrderMapper;

    @Override
    public PageResult<SalesDailyReportDTO> getSalesDailyReport(SalesReportQuery query) {
        // 由于是最小可用版本，这里我们用 MyBatis Plus 的 selectMaps 来聚合数据
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("doc_date as docDate",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(discount_amount) as discountAmount",
                       "sum(actual_amount) as actualAmount")
               .eq("deleted", 0)
               .eq("status", 20) // 仅统计已审核
               .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
               .le(query.getEndDate() != null, "doc_date", query.getEndDate())
               .groupBy("doc_date")
               .orderByDesc("doc_date");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        salesOrderMapper.selectMapsPage(page, wrapper);

        List<SalesDailyReportDTO> list = page.getRecords().stream().map(row -> {
            SalesDailyReportDTO dto = new SalesDailyReportDTO();
            // 注意 JDBC 返回的日期可能需要转换
            Object docDateObj = row.get("docDate");
            if (docDateObj instanceof java.sql.Date) {
                dto.setReportDate(((java.sql.Date) docDateObj).toLocalDate());
            } else if (docDateObj instanceof LocalDate) {
                dto.setReportDate((LocalDate) docDateObj);
            }
            
            dto.setOrderCount(row.get("orderCount") != null ? ((Number) row.get("orderCount")).intValue() : 0);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setDiscountAmount(row.get("discountAmount") != null ? new BigDecimal(row.get("discountAmount").toString()) : BigDecimal.ZERO);
            dto.setActualAmount(row.get("actualAmount") != null ? new BigDecimal(row.get("actualAmount").toString()) : BigDecimal.ZERO);
            
            // 暂不包含退货抵扣（退货在另一张表），后续可联表补充
            dto.setRefundAmount(BigDecimal.ZERO);
            dto.setNetAmount(dto.getActualAmount().subtract(dto.getRefundAmount()));
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public List<SalesDailyReportDTO> exportSalesDailyReport(SalesReportQuery query) {
        query.setPageNum(1);
        query.setPageSize(10000); // 导出时给个大分页
        return getSalesDailyReport(query).getList();
    }
}
