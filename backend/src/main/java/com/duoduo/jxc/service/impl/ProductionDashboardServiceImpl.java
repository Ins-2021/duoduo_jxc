package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.dto.production.ProductionOrderCard;
import com.duoduo.jxc.dto.production.ProductionOrdersResult;
import com.duoduo.jxc.dto.production.ProductionStatistics;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.entity.Process;
import com.duoduo.jxc.entity.ProcessRecord;
import com.duoduo.jxc.entity.ProductionOrder;
import com.duoduo.jxc.entity.QualityCheck;
import com.duoduo.jxc.mapper.*;
import com.duoduo.jxc.service.ProductionDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生产看板服务实现
 */
@Service
public class ProductionDashboardServiceImpl implements ProductionDashboardService {

    @Autowired
    private ProductionOrderMapper orderMapper;
    
    @Autowired
    private ProcessRecordMapper recordMapper;
    
    @Autowired
    private QualityCheckMapper qualityCheckMapper;
    
    @Autowired
    private ProcessMapper processMapper;
    
    @Autowired
    private BundleMapper bundleMapper;

    @Override
    public ProductionStatistics getStatistics(Long factoryId) {
        ProductionStatistics stats = new ProductionStatistics();

        // 生产中订单数
        stats.setInProgressCount(countByStatus(factoryId, "producing"));

        // 待开工订单数
        stats.setPendingCount(countByStatus(factoryId, "pending"));

        // 今日产量
        LocalDate today = LocalDate.now();
        stats.setTodayOutput(sumQuantityByDate(factoryId, today));

        // 本周产量
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        stats.setWeekOutput(sumQuantityByDateRange(factoryId, weekStart, weekEnd));

        // 本月产量
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        stats.setMonthOutput(sumQuantityByDateRange(factoryId, monthStart, monthEnd));

        // 延期订单数
        stats.setDelayedCount(countDelayed(factoryId, today));

        // 质检合格率
        stats.setQualityPassRate(calculatePassRate(factoryId, today));

        return stats;
    }

    @Override
    public ProductionOrdersResult getOrders(Long factoryId) {
        ProductionOrdersResult result = new ProductionOrdersResult();
        result.setPending(getPendingOrders(factoryId));
        result.setInProgress(getInProgressOrders(factoryId));
        result.setCompleted(getCompletedOrders(factoryId, 30));
        return result;
    }

    @Override
    public List<ProductionOrderCard> getPendingOrders(Long factoryId) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getStatus, "pending");
        if (factoryId != null) {
            wrapper.eq(ProductionOrder::getFactoryId, factoryId);
        }
        wrapper.orderByDesc(ProductionOrder::getCreateTime);
        wrapper.last("LIMIT 50");

        List<ProductionOrder> orders = orderMapper.selectList(wrapper);
        return orders.stream().map(this::convertToCard).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrderCard> getInProgressOrders(Long factoryId) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getStatus, "producing");
        if (factoryId != null) {
            wrapper.eq(ProductionOrder::getFactoryId, factoryId);
        }
        wrapper.orderByDesc(ProductionOrder::getCreateTime);
        wrapper.last("LIMIT 50");

        List<ProductionOrder> orders = orderMapper.selectList(wrapper);
        return orders.stream().map(this::convertToCard).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrderCard> getCompletedOrders(Long factoryId, Integer days) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getStatus, "completed");
        if (factoryId != null) {
            wrapper.eq(ProductionOrder::getFactoryId, factoryId);
        }
        if (days != null) {
            LocalDate startDate = LocalDate.now().minusDays(days);
            wrapper.ge(ProductionOrder::getUpdateTime, startDate.atStartOfDay());
        }
        wrapper.orderByDesc(ProductionOrder::getUpdateTime);
        wrapper.last("LIMIT 50");

        List<ProductionOrder> orders = orderMapper.selectList(wrapper);
        return orders.stream().map(this::convertToCard).collect(Collectors.toList());
    }

    private ProductionOrderCard convertToCard(ProductionOrder order) {
        ProductionOrderCard card = new ProductionOrderCard();
        card.setOrderId(order.getOrderId());
        card.setOrderNo(order.getOrderNo());
        card.setStyleCode(order.getStyleNo());
        card.setStyleName(order.getStyleName());
        card.setTotalQuantity(order.getQuantity());
        card.setCompletedQuantity(order.getCompletedQuantity());
        card.setProgress(calculateProgress(order));
        card.setPriority(order.getPriority());
        card.setDeadline(order.getDeadline());
        card.setStatus(order.getStatus());

        // 设置当前工序
        card.setCurrentProcess(getCurrentProcessName(order));

        // 计算延期天数
        if (order.getDeadline() != null && LocalDate.now().isAfter(order.getDeadline())
                && !"completed".equals(order.getStatus())) {
            card.setDelayDays((int) ChronoUnit.DAYS.between(order.getDeadline(), LocalDate.now()));
        }

        return card;
    }

    private Integer calculateProgress(ProductionOrder order) {
        if (order.getQuantity() == null || order.getQuantity() == 0) {
            return 0;
        }
        return (int) ((order.getCompletedQuantity() * 100.0) / order.getQuantity());
    }

    private String getCurrentProcessName(ProductionOrder order) {
        // 获取该订单下扎包中最新计件的工序
        LambdaQueryWrapper<Bundle> bundleWrapper = new LambdaQueryWrapper<>();
        bundleWrapper.eq(Bundle::getOrderId, order.getOrderId());
        List<Bundle> bundles = bundleMapper.selectList(bundleWrapper);

        if (bundles.isEmpty()) {
            return "未开始";
        }

        // 获取这些扎包最近的计件记录
        List<Long> bundleIds = bundles.stream().map(Bundle::getBundleId).collect(Collectors.toList());
        LambdaQueryWrapper<ProcessRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.in(ProcessRecord::getBundleId, bundleIds);
        recordWrapper.orderByDesc(ProcessRecord::getScanTime);
        recordWrapper.last("LIMIT 1");

        ProcessRecord record = recordMapper.selectOne(recordWrapper);
        if (record != null && record.getProcessId() != null) {
            Process process = processMapper.selectById(record.getProcessId());
            return process != null ? process.getProcessName() : "工序" + record.getProcessId();
        }

        return "裁剪中";
    }

    private Integer countByStatus(Long factoryId, String status) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getStatus, status);
        if (factoryId != null) {
            wrapper.eq(ProductionOrder::getFactoryId, factoryId);
        }
        return Math.toIntExact(orderMapper.selectCount(wrapper));
    }

    private Integer countDelayed(Long factoryId, LocalDate today) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(ProductionOrder::getDeadline, today);
        wrapper.ne(ProductionOrder::getStatus, "completed");
        wrapper.ne(ProductionOrder::getStatus, "cancelled");
        if (factoryId != null) {
            wrapper.eq(ProductionOrder::getFactoryId, factoryId);
        }
        return Math.toIntExact(orderMapper.selectCount(wrapper));
    }

    private Integer sumQuantityByDate(Long factoryId, LocalDate date) {
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("DATE(record_time) = {0}", date);
        if (factoryId != null) {
            // 通过bundle关联工厂
            // 这里简化处理，实际可能需要更复杂的查询
        }
        List<ProcessRecord> records = recordMapper.selectList(wrapper);
        return records.stream().mapToInt(ProcessRecord::getQuantity).sum();
    }

    private Integer sumQuantityByDateRange(Long factoryId, LocalDate start, LocalDate end) {
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ProcessRecord::getScanTime, start.atStartOfDay());
        wrapper.le(ProcessRecord::getScanTime, end.atTime(23, 59, 59));
        List<ProcessRecord> records = recordMapper.selectList(wrapper);
        return records.stream().mapToInt(ProcessRecord::getQuantity).sum();
    }

    private Integer calculatePassRate(Long factoryId, LocalDate today) {
        LambdaQueryWrapper<QualityCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("DATE(check_time) = {0}", today);
        List<QualityCheck> checks = qualityCheckMapper.selectList(wrapper);

        if (checks.isEmpty()) {
            return 100; // 没有质检记录时返回100%
        }

        int total = checks.stream().mapToInt(QualityCheck::getCheckQuantity).sum();
        int qualified = checks.stream().mapToInt(QualityCheck::getQualifiedQuantity).sum();

        if (total == 0) {
            return 100;
        }

        return BigDecimal.valueOf(qualified * 100)
                .divide(BigDecimal.valueOf(total), 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
