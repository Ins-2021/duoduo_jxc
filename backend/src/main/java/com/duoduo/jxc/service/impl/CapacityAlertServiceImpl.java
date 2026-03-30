package com.duoduo.jxc.service.impl;

import com.duoduo.jxc.dto.report.CapacityAlertDTO;
import com.duoduo.jxc.dto.report.CapacityStatusDTO;
import com.duoduo.jxc.dto.report.DelayRiskDTO;
import com.duoduo.jxc.dto.report.ProcessBottleneckDTO;
import com.duoduo.jxc.entity.CapacityAlert;
import com.duoduo.jxc.entity.CapacityAlertRule;
import com.duoduo.jxc.entity.ProductionOrder;
import com.duoduo.jxc.mapper.CapacityAlertMapper;
import com.duoduo.jxc.mapper.CapacityAlertRuleMapper;
import com.duoduo.jxc.mapper.ProductionOrderMapper;
import com.duoduo.jxc.service.CapacityAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产能预警服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CapacityAlertServiceImpl implements CapacityAlertService {

    private final CapacityAlertRuleMapper ruleMapper;
    private final CapacityAlertMapper alertMapper;
    private final ProductionOrderMapper orderMapper;

    @Override
    public List<CapacityAlertDTO> getActiveAlerts(Long factoryId) {
        log.info("获取活跃产能预警: factoryId={}", factoryId);

        if (factoryId != null) {
            return alertMapper.selectActiveAlertsByFactory(factoryId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return alertMapper.selectActiveAlerts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void acknowledgeAlert(Long alertId, Long userId) {
        log.info("确认产能预警: alertId={}, userId={}", alertId, userId);
        CapacityAlert alert = alertMapper.selectById(alertId);
        if (alert != null) {
            alert.setStatus("acknowledged");
            alert.setAcknowledgedBy(userId);
            alert.setAcknowledgedAt(java.time.LocalDateTime.now());
            alertMapper.updateById(alert);
        }
    }

    @Override
    public void resolveAlert(Long alertId, Long userId, String resolution) {
        log.info("解决产能预警: alertId={}, userId={}", alertId, userId);
        CapacityAlert alert = alertMapper.selectById(alertId);
        if (alert != null) {
            alert.setStatus("resolved");
            alert.setResolution(resolution);
            alert.setResolvedBy(userId);
            alert.setResolvedAt(java.time.LocalDateTime.now());
            alertMapper.updateById(alert);
        }
    }

    @Override
    public List<CapacityStatusDTO> getProcessCapacityStatus(Long factoryId) {
        log.info("获取工序产能状态: factoryId={}", factoryId);

        List<CapacityStatusDTO> result = new ArrayList<>();

        // 模拟数据
        String[] processNames = {"裁剪", "缝纫", "整烫", "包装"};
        for (int i = 0; i < processNames.length; i++) {
            CapacityStatusDTO dto = new CapacityStatusDTO();
            dto.setFactoryId(factoryId != null ? factoryId : 1L);
            dto.setFactoryName("工厂A");
            dto.setProcessId((long) (i + 1));
            dto.setProcessName(processNames[i]);
            dto.setDailyCapacity(1000 + i * 200);
            dto.setCurrentLoad(800 + i * 150);
            dto.setBacklogQuantity(200 + i * 50);

            // 计算利用率
            BigDecimal utilizationRate = BigDecimal.valueOf(dto.getCurrentLoad())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(dto.getDailyCapacity()), 2, RoundingMode.HALF_UP);
            dto.setUtilizationRate(utilizationRate);

            // 计算积压天数
            int backlogDays = dto.getDailyCapacity() > 0
                    ? dto.getBacklogQuantity() / dto.getDailyCapacity()
                    : 0;
            dto.setBacklogDays(backlogDays);

            // 状态
            if (utilizationRate.compareTo(BigDecimal.valueOf(90)) >= 0) {
                dto.setStatus("critical");
            } else if (utilizationRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
                dto.setStatus("warning");
            } else {
                dto.setStatus("normal");
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<DelayRiskDTO> getDelayRiskOrders(Long factoryId) {
        log.info("获取延期风险订单: factoryId={}", factoryId);

        // 模拟数据
        List<DelayRiskDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= 5; i++) {
            DelayRiskDTO dto = new DelayRiskDTO();
            dto.setOrderId((long) i);
            dto.setOrderNo("PO" + String.format("%05d", i));
            dto.setStyleNo("STYLE" + String.format("%03d", i));
            dto.setStyleName("款式" + i);
            dto.setDeadline(today.plusDays(5 - i));
            dto.setRemainingDays((int) ChronoUnit.DAYS.between(today, dto.getDeadline()));
            dto.setProgress(30 + i * 10);

            // 分析工序瓶颈
            List<ProcessBottleneckDTO> bottlenecks = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                ProcessBottleneckDTO bottleneck = new ProcessBottleneckDTO();
                bottleneck.setProcessId((long) (j + 1));
                bottleneck.setProcessName(new String[]{"裁剪", "缝纫", "整烫"}[j]);
                bottleneck.setPendingQuantity(100 + j * 50);
                bottleneck.setDailyCapacity(500);
                bottleneck.setEstimatedDays(bottleneck.getDailyCapacity() > 0
                        ? bottleneck.getPendingQuantity() / bottleneck.getDailyCapacity()
                        : 0);
                bottlenecks.add(bottleneck);
            }
            dto.setBottlenecks(bottlenecks);

            // 计算延期概率
            int totalEstimatedDays = bottlenecks.stream()
                    .mapToInt(ProcessBottleneckDTO::getEstimatedDays)
                    .sum();

            BigDecimal delayProbability;
            if (dto.getRemainingDays() < 0) {
                delayProbability = BigDecimal.ONE;
            } else if (dto.getRemainingDays() == 0) {
                delayProbability = BigDecimal.valueOf(0.9);
            } else {
                delayProbability = BigDecimal.valueOf(totalEstimatedDays)
                        .divide(BigDecimal.valueOf(dto.getRemainingDays()), 2, RoundingMode.HALF_UP)
                        .min(BigDecimal.ONE);
            }
            dto.setDelayProbability(delayProbability);

            // 判断风险等级
            if (delayProbability.compareTo(BigDecimal.valueOf(0.7)) >= 0) {
                dto.setRiskLevel("high");
                dto.setSuggestion("建议立即加派人手或调整排程");
            } else if (delayProbability.compareTo(BigDecimal.valueOf(0.4)) >= 0) {
                dto.setRiskLevel("medium");
                dto.setSuggestion("建议关注进度，必要时提前干预");
            } else {
                dto.setRiskLevel("low");
                dto.setSuggestion("正常推进即可");
            }

            result.add(dto);
        }

        // 只返回高风险和中风险订单
        return result.stream()
                .filter(dto -> !"low".equals(dto.getRiskLevel()))
                .sorted(Comparator.comparing(DelayRiskDTO::getDelayProbability).reversed())
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void checkAndGenerateAlerts() {
        log.info("执行产能预警检查任务");

        List<CapacityAlertRule> rules = ruleMapper.selectActiveRules();
        for (CapacityAlertRule rule : rules) {
            try {
                checkRule(rule);
            } catch (Exception e) {
                log.error("检查预警规则失败: ruleId={}, error={}", rule.getId(), e.getMessage());
            }
        }
    }

    private void checkRule(CapacityAlertRule rule) {
        String metricType = rule.getMetricType();
        switch (metricType) {
            case "utilization":
                checkUtilization(rule);
                break;
            case "backlog":
                checkBacklog(rule);
                break;
            default:
                log.warn("未知的预警规则类型: {}", metricType);
        }
    }

    private void checkUtilization(CapacityAlertRule rule) {
        // 模拟计算产能利用率
        BigDecimal utilization = BigDecimal.valueOf(Math.random() * 100);

        if (utilization.compareTo(rule.getCriticalThreshold()) >= 0) {
            createAlert(rule, "critical", utilization, rule.getCriticalThreshold());
        } else if (utilization.compareTo(rule.getWarningThreshold()) >= 0) {
            createAlert(rule, "warning", utilization, rule.getWarningThreshold());
        }
    }

    private void checkBacklog(CapacityAlertRule rule) {
        // 模拟计算积压天数
        int backlog = (int) (Math.random() * 10);
        int dailyCapacity = 100;

        BigDecimal backlogDays = dailyCapacity > 0
                ? BigDecimal.valueOf(backlog).divide(BigDecimal.valueOf(dailyCapacity), 2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(Integer.MAX_VALUE);

        if (backlogDays.compareTo(rule.getCriticalThreshold()) >= 0) {
            createAlert(rule, "critical", backlogDays, rule.getCriticalThreshold());
        } else if (backlogDays.compareTo(rule.getWarningThreshold()) >= 0) {
            createAlert(rule, "warning", backlogDays, rule.getWarningThreshold());
        }
    }

    private void createAlert(CapacityAlertRule rule, String level, BigDecimal value, BigDecimal threshold) {
        CapacityAlert alert = new CapacityAlert();
        alert.setAlertType(rule.getMetricType());
        alert.setAlertLevel(level);
        alert.setMessage(String.format("%s - %s 超出阈值: %.2f (阈值: %.2f)",
                rule.getRuleName(), rule.getMetricType(), value, threshold));
        alert.setFactoryId(rule.getFactoryId());
        alert.setFactoryName("工厂A");
        alert.setWorkGroupId(rule.getWorkGroupId());
        alert.setProcessId(rule.getProcessId());
        alert.setMetricValue(value);
        alert.setThreshold(threshold);
        alert.setStatus("active");
        alertMapper.insert(alert);

        log.info("生成产能预警: type={}, level={}, value={}", rule.getMetricType(), level, value);
    }

    private CapacityAlertDTO convertToDTO(CapacityAlert alert) {
        CapacityAlertDTO dto = new CapacityAlertDTO();
        dto.setAlertId(alert.getId());
        dto.setAlertType(alert.getAlertType());
        dto.setAlertLevel(alert.getAlertLevel());
        dto.setMessage(alert.getMessage());
        dto.setFactoryId(alert.getFactoryId());
        dto.setFactoryName(alert.getFactoryName());
        dto.setWorkGroupId(alert.getWorkGroupId());
        dto.setWorkGroupName(alert.getWorkGroupName());
        dto.setProcessId(alert.getProcessId());
        dto.setProcessName(alert.getProcessName());
        dto.setOrderId(alert.getOrderId());
        dto.setOrderNo(alert.getOrderNo());
        dto.setMetricValue(alert.getMetricValue());
        dto.setThreshold(alert.getThreshold());
        dto.setStatus(alert.getStatus());
        dto.setCreatedAt(alert.getCreateTime());
        return dto;
    }
}
