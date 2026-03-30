import request from '@/utils/request'

/**
 * 生产统计数据
 */
export interface ProductionStatistics {
  inProgressCount: number
  pendingCount: number
  todayOutput: number
  weekOutput: number
  monthOutput: number
  todayEfficiency?: number
  delayedCount: number
  qualityPassRate: number
}

/**
 * 生产订单卡片
 */
export interface ProductionOrderCard {
  orderId: number
  orderNo: string
  styleCode: string
  styleName: string
  totalQuantity: number
  completedQuantity: number
  progress: number
  currentProcess: string
  priority: string
  deadline: string
  delayDays?: number
  status: string
}

/**
 * 订单列表结果
 */
export interface ProductionOrdersResult {
  pending: ProductionOrderCard[]
  inProgress: ProductionOrderCard[]
  completed: ProductionOrderCard[]
}

/**
 * 获取统计数据
 */
export function getProductionStatistics(factoryId?: number): Promise<ProductionStatistics> {
  return request({
    url: '/api/production/dashboard/statistics',
    method: 'get',
    params: { factoryId }
  })
}

/**
 * 获取订单列表
 */
export function getProductionOrders(factoryId?: number): Promise<ProductionOrdersResult> {
  return request({
    url: '/api/production/dashboard/orders',
    method: 'get',
    params: { factoryId }
  })
}
