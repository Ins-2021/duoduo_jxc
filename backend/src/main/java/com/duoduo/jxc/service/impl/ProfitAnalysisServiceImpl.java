package com.duoduo.jxc.service.impl;

import com.duoduo.jxc.dto.report.*;
import com.duoduo.jxc.mapper.CustomerMapper;
import com.duoduo.jxc.mapper.ProductionOrderMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.mapper.StyleMapper;
import com.duoduo.jxc.service.ProfitAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 利润分析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfitAnalysisServiceImpl implements ProfitAnalysisService {

    private final SalesOrderMapper salesOrderMapper;
    private final ProductionOrderMapper productionOrderMapper;
    private final StyleMapper styleMapper;
    private final CustomerMapper customerMapper;

    @Override
    public List<StyleProfitDTO> getStyleProfitRanking(ProfitQueryDTO query) {
        log.info("获取款式利润排行: {} - {}", query.getStartDate(), query.getEndDate());

        List<StyleProfitDTO> result = new ArrayList<>();

        // 模拟数据 - 实际项目中应从数据库查询
        for (int i = 1; i <= 10; i++) {
            StyleProfitDTO dto = new StyleProfitDTO();
            dto.setStyleId((long) i);
            dto.setStyleNo("STYLE" + String.format("%03d", i));
            dto.setStyleName("款式" + i);
            dto.setSalesQuantity(1000 + i * 100);
            dto.setSalesAmount(BigDecimal.valueOf(50000 + i * 5000));
            dto.setCostAmount(BigDecimal.valueOf(35000 + i * 3500));
            dto.setGrossProfit(dto.getSalesAmount().subtract(dto.getCostAmount()));
            dto.setGrossProfitRate(dto.getGrossProfit()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(dto.getSalesAmount(), 2, RoundingMode.HALF_UP));
            dto.setOrderCount(5 + i);
            result.add(dto);
        }

        // 按毛利排序
        String orderBy = query.getOrderBy() != null ? query.getOrderBy() : "profit";
        Comparator<StyleProfitDTO> comparator = switch (orderBy) {
            case "amount" -> Comparator.comparing(StyleProfitDTO::getSalesAmount);
            case "quantity" -> Comparator.comparing(StyleProfitDTO::getSalesQuantity);
            default -> Comparator.comparing(StyleProfitDTO::getGrossProfit);
        };

        result = result.stream()
                .sorted(comparator.reversed())
                .limit(query.getLimit() != null ? query.getLimit() : 20)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<CustomerProfitDTO> getCustomerProfitRanking(ProfitQueryDTO query) {
        log.info("获取客户利润排行: {} - {}", query.getStartDate(), query.getEndDate());

        List<CustomerProfitDTO> result = new ArrayList<>();

        // 模拟数据
        for (int i = 1; i <= 8; i++) {
            CustomerProfitDTO dto = new CustomerProfitDTO();
            dto.setCustomerId((long) i);
            dto.setCustomerCode("CUST" + String.format("%03d", i));
            dto.setCustomerName("客户" + i);
            dto.setSalesQuantity(2000 + i * 200);
            dto.setSalesAmount(BigDecimal.valueOf(100000 + i * 10000));
            dto.setCostAmount(BigDecimal.valueOf(70000 + i * 7000));
            dto.setGrossProfit(dto.getSalesAmount().subtract(dto.getCostAmount()));
            dto.setGrossProfitRate(dto.getGrossProfit()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(dto.getSalesAmount(), 2, RoundingMode.HALF_UP));
            dto.setOrderCount(10 + i * 2);
            result.add(dto);
        }

        return result.stream()
                .sorted(Comparator.comparing(CustomerProfitDTO::getGrossProfit).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public ProfitTrendDTO getProfitTrend(ProfitQueryDTO query) {
        log.info("获取利润趋势: {} - {}", query.getStartDate(), query.getEndDate());

        ProfitTrendDTO trend = new ProfitTrendDTO();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> salesAmount = new ArrayList<>();
        List<BigDecimal> costAmount = new ArrayList<>();
        List<BigDecimal> grossProfit = new ArrayList<>();

        LocalDate startDate = query.getStartDate() != null ? query.getStartDate() : LocalDate.now().minusMonths(6);
        LocalDate endDate = query.getEndDate() != null ? query.getEndDate() : LocalDate.now();

        // 按月生成趋势数据
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        while (!current.isAfter(end)) {
            String month = current.format(formatter);
            dates.add(month);

            // 模拟数据
            BigDecimal sales = BigDecimal.valueOf(50000 + Math.random() * 20000);
            BigDecimal cost = sales.multiply(BigDecimal.valueOf(0.7));
            BigDecimal profit = sales.subtract(cost);

            salesAmount.add(sales);
            costAmount.add(cost);
            grossProfit.add(profit);

            totalSales = totalSales.add(sales);
            totalCost = totalCost.add(cost);
            totalProfit = totalProfit.add(profit);

            current = current.plusMonths(1);
        }

        trend.setDates(dates);
        trend.setSalesAmount(salesAmount);
        trend.setCostAmount(costAmount);
        trend.setGrossProfit(grossProfit);
        trend.setTotalSalesAmount(totalSales);
        trend.setTotalCostAmount(totalCost);
        trend.setTotalGrossProfit(totalProfit);

        if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
            trend.setAvgGrossProfitRate(totalProfit
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalSales, 2, RoundingMode.HALF_UP));
        } else {
            trend.setAvgGrossProfitRate(BigDecimal.ZERO);
        }

        return trend;
    }

    @Override
    public ProfitSummaryDTO getProfitSummary(ProfitQueryDTO query) {
        log.info("获取利润汇总: {} - {}", query.getStartDate(), query.getEndDate());

        ProfitTrendDTO trend = getProfitTrend(query);

        ProfitSummaryDTO summary = new ProfitSummaryDTO();
        summary.setTotalSalesAmount(trend.getTotalSalesAmount());
        summary.setTotalCostAmount(trend.getTotalCostAmount());
        summary.setTotalGrossProfit(trend.getTotalGrossProfit());
        summary.setAvgGrossProfitRate(trend.getAvgGrossProfitRate());
        summary.setTotalOrderCount(156);
        summary.setTotalSalesQuantity(3250);

        return summary;
    }

    @Override
    public StyleProfitDetailDTO getStyleProfitDetail(Long styleId, ProfitQueryDTO query) {
        log.info("获取款式利润详情: styleId={}", styleId);

        StyleProfitDetailDTO detail = new StyleProfitDetailDTO();

        // 汇总数据
        StyleProfitDTO summary = new StyleProfitDTO();
        summary.setStyleId(styleId);
        summary.setStyleNo("STYLE001");
        summary.setStyleName("示例款式");
        summary.setSalesQuantity(1000);
        summary.setSalesAmount(BigDecimal.valueOf(50000));
        summary.setCostAmount(BigDecimal.valueOf(35000));
        summary.setGrossProfit(summary.getSalesAmount().subtract(summary.getCostAmount()));
        summary.setGrossProfitRate(BigDecimal.valueOf(30.00));
        summary.setOrderCount(10);
        detail.setSummary(summary);

        // 按颜色分析
        List<ColorProfitDTO> byColor = new ArrayList<>();
        String[] colors = {"红色", "蓝色", "黑色", "白色"};
        for (String color : colors) {
            ColorProfitDTO colorDto = new ColorProfitDTO();
            colorDto.setColorCode(color);
            colorDto.setColorName(color);
            colorDto.setSalesQuantity(250);
            colorDto.setSalesAmount(BigDecimal.valueOf(12500));
            colorDto.setCostAmount(BigDecimal.valueOf(8750));
            colorDto.setGrossProfit(colorDto.getSalesAmount().subtract(colorDto.getCostAmount()));
            colorDto.setGrossProfitRate(BigDecimal.valueOf(30.00));
            byColor.add(colorDto);
        }
        detail.setByColor(byColor);

        // 按尺码分析
        List<SizeProfitDTO> bySize = new ArrayList<>();
        String[] sizes = {"S", "M", "L", "XL"};
        for (String size : sizes) {
            SizeProfitDTO sizeDto = new SizeProfitDTO();
            sizeDto.setSizeName(size);
            sizeDto.setSalesQuantity(250);
            sizeDto.setSalesAmount(BigDecimal.valueOf(12500));
            sizeDto.setCostAmount(BigDecimal.valueOf(8750));
            sizeDto.setGrossProfit(sizeDto.getSalesAmount().subtract(sizeDto.getCostAmount()));
            sizeDto.setGrossProfitRate(BigDecimal.valueOf(30.00));
            bySize.add(sizeDto);
        }
        detail.setBySize(bySize);

        // 按月度趋势
        List<MonthlyProfitDTO> byMonth = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            MonthlyProfitDTO monthDto = new MonthlyProfitDTO();
            monthDto.setMonth("2024-" + String.format("%02d", i));
            monthDto.setSalesQuantity(167);
            monthDto.setSalesAmount(BigDecimal.valueOf(8333));
            monthDto.setCostAmount(BigDecimal.valueOf(5833));
            monthDto.setGrossProfit(monthDto.getSalesAmount().subtract(monthDto.getCostAmount()));
            monthDto.setGrossProfitRate(BigDecimal.valueOf(30.00));
            byMonth.add(monthDto);
        }
        detail.setByMonth(byMonth);

        return detail;
    }
}
