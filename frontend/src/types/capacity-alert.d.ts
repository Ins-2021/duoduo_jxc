// 产能预警类型定义

export interface CapacityAlert {
  alertId: number
  alertType: string
  alertLevel: 'warning' | 'critical'
  message: string
  factoryId?: number
  factoryName?: string
  workGroupId?: number
  workGroupName?: string
  processId?: number
  processName?: string
  orderId?: number
  orderNo?: string
  metricValue?: number
  threshold?: number
  status: string
  createdAt?: string
}

export interface CapacityStatus {
  factoryId: number
  factoryName: string
  processId: number
  processName: string
  dailyCapacity: number
  currentLoad: number
  utilizationRate: number
  backlogQuantity: number
  backlogDays: number
  status: 'normal' | 'warning' | 'critical'
}

export interface ProcessBottleneck {
  processId: number
  processName: string
  pendingQuantity: number
  dailyCapacity: number
  estimatedDays: number
}

export interface DelayRisk {
  orderId: number
  orderNo: string
  styleNo: string
  styleName: string
  deadline: string
  remainingDays: number
  progress: number
  delayProbability: number
  riskLevel: 'high' | 'medium' | 'low'
  suggestion: string
  bottlenecks: ProcessBottleneck[]
}
