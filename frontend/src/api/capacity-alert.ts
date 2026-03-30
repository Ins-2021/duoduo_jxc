import request from '@/utils/request'

// 获取活跃预警
export function getActiveAlerts(factoryId?: number) {
  return request({
    url: '/capacity-alert/alerts',
    method: 'get',
    params: { factoryId }
  })
}

// 确认预警
export function acknowledgeAlert(alertId: number, userId: number) {
  return request({
    url: `/capacity-alert/alerts/${alertId}/acknowledge`,
    method: 'post',
    params: { userId }
  })
}

// 解决预警
export function resolveAlert(alertId: number, userId: number, resolution: string) {
  return request({
    url: `/capacity-alert/alerts/${alertId}/resolve`,
    method: 'post',
    params: { userId, resolution }
  })
}

// 获取工序产能状态
export function getProcessCapacityStatus(factoryId?: number) {
  return request({
    url: '/capacity-alert/capacity-status',
    method: 'get',
    params: { factoryId }
  })
}

// 获取延期风险订单
export function getDelayRiskOrders(factoryId?: number) {
  return request({
    url: '/capacity-alert/delay-risks',
    method: 'get',
    params: { factoryId }
  })
}
