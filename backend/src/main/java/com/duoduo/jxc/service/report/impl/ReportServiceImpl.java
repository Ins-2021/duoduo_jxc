package com.duoduo.jxc.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.report.*;
import com.duoduo.jxc.entity.*;
import com.duoduo.jxc.mapper.*;
import com.duoduo.jxc.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SalesOrderMapper salesOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final ProductionOrderMapper productionOrderMapper;
    private final CustomerMapper customerMapper;
    private final SupplierMapper supplierMapper;

    // ========== 销售报表 ==========

    @Override
    public PageResult<SalesDailyReportDTO> getSalesDailyReport(SalesReportQuery query) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("doc_date as docDate",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(discount_amount) as discountAmount",
                       "sum(actual_amount) as actualAmount")
                .eq("deleted", 0)
                .eq("status", 20)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .groupBy("doc_date")
                .orderByDesc("doc_date");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        salesOrderMapper.selectMapsPage(page, wrapper);

        List<SalesDailyReportDTO> list = page.getRecords().stream().map(row -> {
            SalesDailyReportDTO dto = new SalesDailyReportDTO();
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
            dto.setRefundAmount(BigDecimal.ZERO);
            dto.setNetAmount(dto.getActualAmount().subtract(dto.getRefundAmount()));
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public List<SalesDailyReportDTO> exportSalesDailyReport(SalesReportQuery query) {
        query.setPageNum(1);
        query.setPageSize(10000);
        return getSalesDailyReport(query).getList();
    }

    @Override
    public PageResult<SalesMonthlyReportDTO> getSalesMonthlyReport(ReportQuery query) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("DATE_FORMAT(doc_date, '%Y-%m') as reportMonth",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(actual_amount) as actualAmount",
                       "sum(paid_amount) as paidAmount",
                       "count(distinct customer_id) as customerCount")
                .eq("deleted", 0)
                .eq("status", 20)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .groupBy("DATE_FORMAT(doc_date, '%Y-%m')")
                .orderByDesc("reportMonth");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        salesOrderMapper.selectMapsPage(page, wrapper);

        List<SalesMonthlyReportDTO> list = page.getRecords().stream().map(row -> {
            SalesMonthlyReportDTO dto = new SalesMonthlyReportDTO();
            String monthStr = (String) row.get("reportMonth");
            if (monthStr != null) {
                dto.setReportMonth(YearMonth.parse(monthStr));
            }
            dto.setOrderCount(row.get("orderCount") != null ? ((Number) row.get("orderCount")).intValue() : 0);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setActualAmount(row.get("actualAmount") != null ? new BigDecimal(row.get("actualAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setCustomerCount(row.get("customerCount") != null ? ((Number) row.get("customerCount")).intValue() : 0);
            if (dto.getOrderCount() > 0) {
                dto.setAvgOrderAmount(dto.getActualAmount().divide(new BigDecimal(dto.getOrderCount()), 2, RoundingMode.HALF_UP));
            } else {
                dto.setAvgOrderAmount(BigDecimal.ZERO);
            }
            dto.setMonthOnMonthRate(BigDecimal.ZERO);
            dto.setYearOnYearRate(BigDecimal.ZERO);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public PageResult<SalesRankingReportDTO> getSalesRankingReport(ReportQuery query) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("customer_id as customerId",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(paid_amount) as paidAmount")
                .eq("deleted", 0)
                .eq("status", 20)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .isNotNull("customer_id")
                .groupBy("customer_id")
                .orderByDesc("totalAmount");

        Integer limit = query.getLimit() != null ? query.getLimit() : 20;
        wrapper.last("LIMIT " + limit);

        List<Map<String, Object>> records = salesOrderMapper.selectMaps(wrapper);
        AtomicInteger rankCounter = new AtomicInteger(1);

        List<SalesRankingReportDTO> list = records.stream().map(row -> {
            SalesRankingReportDTO dto = new SalesRankingReportDTO();
            dto.setRank(rankCounter.getAndIncrement());
            Long customerId = row.get("customerId") != null ? ((Number) row.get("customerId")).longValue() : null;
            dto.setCustomerId(customerId);
            dto.setOrderCount(row.get("orderCount") != null ? ((Number) row.get("orderCount")).intValue() : 0);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setUnpaidAmount(dto.getTotalAmount().subtract(dto.getPaidAmount()));
            if (customerId != null) {
                Customer customer = customerMapper.selectById(customerId);
                if (customer != null) {
                    dto.setCustomerName(customer.getCustomerName());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>((long) list.size(), list);
    }

    // ========== 采购报表 ==========

    @Override
    public PageResult<PurchaseDailyReportDTO> getPurchaseDailyReport(ReportQuery query) {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("doc_date as docDate",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(actual_amount) as actualAmount",
                       "sum(paid_amount) as paidAmount",
                       "count(distinct supplier_id) as supplierCount")
                .eq("deleted", 0)
                .eq("status", 20)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .groupBy("doc_date")
                .orderByDesc("doc_date");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        purchaseOrderMapper.selectMapsPage(page, wrapper);

        List<PurchaseDailyReportDTO> list = page.getRecords().stream().map(row -> {
            PurchaseDailyReportDTO dto = new PurchaseDailyReportDTO();
            Object docDateObj = row.get("docDate");
            if (docDateObj instanceof java.sql.Date) {
                dto.setReportDate(((java.sql.Date) docDateObj).toLocalDate());
            } else if (docDateObj instanceof LocalDate) {
                dto.setReportDate((LocalDate) docDateObj);
            }
            dto.setOrderCount(row.get("orderCount") != null ? ((Number) row.get("orderCount")).intValue() : 0);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setActualAmount(row.get("actualAmount") != null ? new BigDecimal(row.get("actualAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setSupplierCount(row.get("supplierCount") != null ? ((Number) row.get("supplierCount")).intValue() : 0);
            if (dto.getOrderCount() > 0) {
                dto.setAvgOrderAmount(dto.getActualAmount().divide(new BigDecimal(dto.getOrderCount()), 2, RoundingMode.HALF_UP));
            } else {
                dto.setAvgOrderAmount(BigDecimal.ZERO);
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public PageResult<SupplierAnalysisDTO> getSupplierAnalysisReport(ReportQuery query) {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("supplier_id as supplierId",
                       "count(order_id) as orderCount",
                       "sum(total_amount) as totalAmount",
                       "sum(paid_amount) as paidAmount")
                .eq("deleted", 0)
                .eq("status", 20)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .isNotNull("supplier_id")
                .groupBy("supplier_id")
                .orderByDesc("totalAmount");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        purchaseOrderMapper.selectMapsPage(page, wrapper);

        List<SupplierAnalysisDTO> list = page.getRecords().stream().map(row -> {
            SupplierAnalysisDTO dto = new SupplierAnalysisDTO();
            Long supplierId = row.get("supplierId") != null ? ((Number) row.get("supplierId")).longValue() : null;
            dto.setSupplierId(supplierId);
            dto.setOrderCount(row.get("orderCount") != null ? ((Number) row.get("orderCount")).intValue() : 0);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setUnpaidAmount(dto.getTotalAmount().subtract(dto.getPaidAmount()));
            dto.setOnTimeRate(BigDecimal.valueOf(95.00));
            dto.setQualityRate(BigDecimal.valueOf(98.00));
            if (supplierId != null) {
                Supplier supplier = supplierMapper.selectById(supplierId);
                if (supplier != null) {
                    dto.setSupplierName(supplier.getSupplierName());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    // ========== 库存报表 ==========

    @Override
    public PageResult<InventoryTurnoverDTO> getInventoryTurnoverReport(ReportQuery query) {
        return new PageResult<>(0L, List.of());
    }

    @Override
    public PageResult<InventoryAgeDTO> getInventoryAgeReport(ReportQuery query) {
        return new PageResult<>(0L, List.of());
    }

    // ========== 生产报表 ==========

    @Override
    public PageResult<ProductionEfficiencyDTO> getProductionEfficiencyReport(ReportQuery query) {
        QueryWrapper<ProductionOrder> wrapper = new QueryWrapper<>();
        wrapper.select("DATE(create_time) as reportDate",
                       "sum(quantity) as planQuantity",
                       "sum(completed_quantity) as actualQuantity")
                .isNotNull("create_time")
                .ge(query.getStartDate() != null, "create_time", query.getStartDate().atStartOfDay())
                .le(query.getEndDate() != null, "create_time", query.getEndDate().atTime(23, 59, 59))
                .groupBy("DATE(create_time)")
                .orderByDesc("reportDate");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        productionOrderMapper.selectMapsPage(page, wrapper);

        List<ProductionEfficiencyDTO> list = page.getRecords().stream().map(row -> {
            ProductionEfficiencyDTO dto = new ProductionEfficiencyDTO();
            Object reportDateObj = row.get("reportDate");
            if (reportDateObj instanceof java.sql.Date) {
                dto.setReportDate(((java.sql.Date) reportDateObj).toLocalDate());
            } else if (reportDateObj instanceof LocalDate) {
                dto.setReportDate((LocalDate) reportDateObj);
            }
            dto.setPlanQuantity(row.get("planQuantity") != null ? ((Number) row.get("planQuantity")).intValue() : 0);
            dto.setActualQuantity(row.get("actualQuantity") != null ? ((Number) row.get("actualQuantity")).intValue() : 0);
            if (dto.getPlanQuantity() > 0) {
                dto.setCompletionRate(BigDecimal.valueOf(dto.getActualQuantity() * 100.0 / dto.getPlanQuantity()).setScale(2, RoundingMode.HALF_UP));
            } else {
                dto.setCompletionRate(BigDecimal.ZERO);
            }
            dto.setQualifiedQuantity(dto.getActualQuantity());
            dto.setQualityRate(BigDecimal.valueOf(100.00));
            dto.setWorkerCount(0);
            dto.setAvgOutput(BigDecimal.ZERO);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public PageResult<QualityAnalysisDTO> getQualityAnalysisReport(ReportQuery query) {
        return new PageResult<>(0L, List.of());
    }

    // ========== 财务报表 ==========

    @Override
    public PageResult<ReceivableReportDTO> getReceivableReport(ReportQuery query) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("customer_id as customerId",
                       "sum(actual_amount) as totalAmount",
                       "sum(paid_amount) as paidAmount")
                .eq("deleted", 0)
                .in("status", 20, 30, 40)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .isNotNull("customer_id")
                .groupBy("customer_id")
                .having("sum(actual_amount) > sum(paid_amount)")
                .orderByDesc("sum(actual_amount) - sum(paid_amount)");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        salesOrderMapper.selectMapsPage(page, wrapper);

        List<ReceivableReportDTO> list = page.getRecords().stream().map(row -> {
            ReceivableReportDTO dto = new ReceivableReportDTO();
            Long customerId = row.get("customerId") != null ? ((Number) row.get("customerId")).longValue() : null;
            dto.setCustomerId(customerId);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setUnpaidAmount(dto.getTotalAmount().subtract(dto.getPaidAmount()));
            dto.setOverdueAmount(BigDecimal.ZERO);
            dto.setOverdueDays(0);
            if (customerId != null) {
                Customer customer = customerMapper.selectById(customerId);
                if (customer != null) {
                    dto.setCustomerName(customer.getCustomerName());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public PageResult<PayableReportDTO> getPayableReport(ReportQuery query) {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("supplier_id as supplierId",
                       "sum(actual_amount) as totalAmount",
                       "sum(paid_amount) as paidAmount")
                .eq("deleted", 0)
                .in("status", 20, 30, 40)
                .ge(query.getStartDate() != null, "doc_date", query.getStartDate())
                .le(query.getEndDate() != null, "doc_date", query.getEndDate())
                .isNotNull("supplier_id")
                .groupBy("supplier_id")
                .having("sum(actual_amount) > sum(paid_amount)")
                .orderByDesc("sum(actual_amount) - sum(paid_amount)");

        Page<Map<String, Object>> page = new Page<>(query.getPageNum(), query.getPageSize());
        purchaseOrderMapper.selectMapsPage(page, wrapper);

        List<PayableReportDTO> list = page.getRecords().stream().map(row -> {
            PayableReportDTO dto = new PayableReportDTO();
            Long supplierId = row.get("supplierId") != null ? ((Number) row.get("supplierId")).longValue() : null;
            dto.setSupplierId(supplierId);
            dto.setTotalAmount(row.get("totalAmount") != null ? new BigDecimal(row.get("totalAmount").toString()) : BigDecimal.ZERO);
            dto.setPaidAmount(row.get("paidAmount") != null ? new BigDecimal(row.get("paidAmount").toString()) : BigDecimal.ZERO);
            dto.setUnpaidAmount(dto.getTotalAmount().subtract(dto.getPaidAmount()));
            dto.setOverdueAmount(BigDecimal.ZERO);
            dto.setOverdueDays(0);
            if (supplierId != null) {
                Supplier supplier = supplierMapper.selectById(supplierId);
                if (supplier != null) {
                    dto.setSupplierName(supplier.getSupplierName());
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), list);
    }

    // ========== 工资报表 ==========

    @Override
    public PageResult<PieceWageSummaryDTO> getPieceWageSummaryReport(ReportQuery query) {
        return new PageResult<>(0L, List.of());
    }
}
