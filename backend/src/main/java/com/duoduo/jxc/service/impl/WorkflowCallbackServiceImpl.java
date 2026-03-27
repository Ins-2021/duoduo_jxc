package com.duoduo.jxc.service.impl;

import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.workflow.WorkflowCallbackDTO;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.WorkflowCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工作流回调服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowCallbackServiceImpl implements WorkflowCallbackService {

    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderDetailMapper detailMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onProcessCompleted(WorkflowCallbackDTO dto) {
        log.info("流程完成回调: processInstanceId={}, bizType={}, bizId={}",
                dto.getProcessInstanceId(), dto.getBizType(), dto.getBizId());

        if (!"SALES_ORDER".equals(dto.getBizType())) {
            log.warn("暂不处理非销售单业务类型: {}", dto.getBizType());
            return;
        }

        Long orderId = Long.valueOf(dto.getBizId());

        // 查询销售单
        SalesOrder order = salesOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }

        // 只有状态为30（执行中）时才更新为40（已完成）
        if (order.getStatus() != null && order.getStatus() == 30) {
            // 如果是预订单，需要解锁剩余锁定库存
            if (order.getOrderType() != null && order.getOrderType() == 3) {
                unlockRemainingStock(orderId);
            }

            // 更新状态为已完成
            SalesOrder updateOrder = new SalesOrder();
            updateOrder.setOrderId(orderId);
            updateOrder.setStatus(40);
            salesOrderMapper.updateById(updateOrder);

            log.info("销售单状态已更新为已完成: orderId={}", orderId);
        }
    }

    @Override
    public void onTaskApproved(WorkflowCallbackDTO dto) {
        log.info("任务通过回调: processInstanceId={}, taskId={}, approverId={}",
                dto.getProcessInstanceId(), dto.getApproverId());
        // 任务级回调可根据业务需要实现
    }

    @Override
    public void onTaskRejected(WorkflowCallbackDTO dto) {
        log.info("任务驳回回调: processInstanceId={}, taskId={}, approverId={}",
                dto.getProcessInstanceId(), dto.getApproverId());
        // 任务级回调可根据业务需要实现
    }

    /**
     * 解锁预订单剩余锁定库存
     */
    private void unlockRemainingStock(Long orderId) {
        LambdaQueryWrapper<SalesOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrderDetail::getOrderId, orderId);
        List<SalesOrderDetail> details = detailMapper.selectList(wrapper);

        for (SalesOrderDetail detail : details) {
            if (detail.getWarehouseId() != null && detail.getSkuId() != null
                    && detail.getUnfulfilledQty() != null && detail.getUnfulfilledQty() > 0) {
                inventoryService.unlockStock(
                        detail.getWarehouseId(),
                        detail.getSkuId(),
                        detail.getUnfulfilledQty(),
                        orderId
                );
                log.info("解锁预订单剩余库存: orderId={}, skuId={}, qty={}",
                        orderId, detail.getSkuId(), detail.getUnfulfilledQty());
            }
        }
    }
}
