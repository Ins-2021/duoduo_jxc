package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.report.CapacityAlertDTO;
import com.duoduo.jxc.dto.report.CapacityStatusDTO;
import com.duoduo.jxc.dto.report.DelayRiskDTO;

import java.util.List;

/**
 * 产能预警服务
 */
public interface CapacityAlertService {

    /**
     * 获取活跃预警
     *
     * @param factoryId 工厂ID
     * @return 预警列表
     */
    List<CapacityAlertDTO> getActiveAlerts(Long factoryId);

    /**
     * 确认预警
     *
     * @param alertId 预警ID
     * @param userId  用户ID
     */
    void acknowledgeAlert(Long alertId, Long userId);

    /**
     * 解决预警
     *
     * @param alertId    预警ID
     * @param userId     用户ID
     * @param resolution 解决方案
     */
    void resolveAlert(Long alertId, Long userId, String resolution);

    /**
     * 获取工序产能状态
     *
     * @param factoryId 工厂ID
     * @return 产能状态列表
     */
    List<CapacityStatusDTO> getProcessCapacityStatus(Long factoryId);

    /**
     * 获取延期风险订单
     *
     * @param factoryId 工厂ID
     * @return 延期风险订单列表
     */
    List<DelayRiskDTO> getDelayRiskOrders(Long factoryId);

    /**
     * 检查并生成预警
     */
    void checkAndGenerateAlerts();
}
