import request from '@/utils/request'

// Production Order API
export function getProductionOrderList(params: any) {
  return request({ url: '/production/order/page', method: 'get', params })
}
export function getProductionOrder(id: number) {
  return request({ url: `/production/order/${id}`, method: 'get' })
}
export function createProductionOrder(data: any) {
  return request({ url: '/production/order', method: 'post', data })
}
export function updateProductionOrder(id: number, data: any) {
  return request({ url: `/production/order/${id}`, method: 'put', data })
}
export function deleteProductionOrder(id: number) {
  return request({ url: `/production/order/${id}`, method: 'delete' })
}
export function updateProductionOrderStatus(id: number, status: string) {
  return request({ url: `/production/order/${id}/status`, method: 'put', params: { status } })
}
export function calculateMaterialRequirement(id: number) {
  return request({ url: `/production/order/${id}/material-requirement`, method: 'get' })
}
export function productionInbound(id: number, data: any) {
  return request({ url: `/production/order/${id}/inbound`, method: 'post', data })
}

// Production Plan API
export function getProductionPlanList(params: any) {
  return request({ url: '/production/plan/page', method: 'get', params })
}
export function getProductionPlan(id: number) {
  return request({ url: `/production/plan/${id}`, method: 'get' })
}
export function createProductionPlan(data: any) {
  return request({ url: '/production/plan', method: 'post', data })
}
export function updateProductionPlan(id: number, data: any) {
  return request({ url: `/production/plan/${id}`, method: 'put', data })
}
export function deleteProductionPlan(id: number) {
  return request({ url: `/production/plan/${id}`, method: 'delete' })
}

// Production Schedule API
export function getProductionScheduleList(params: any) {
  return request({ url: '/production/schedule/page', method: 'get', params })
}
export function getProductionSchedule(id: number) {
  return request({ url: `/production/schedule/${id}`, method: 'get' })
}
export function createProductionSchedule(data: any) {
  return request({ url: '/production/schedule', method: 'post', data })
}
export function updateProductionSchedule(id: number, data: any) {
  return request({ url: `/production/schedule/${id}`, method: 'put', data })
}
export function deleteProductionSchedule(id: number) {
  return request({ url: `/production/schedule/${id}`, method: 'delete' })
}

// Cut Order API
export function getCutOrderList(params: any) {
  return request({ url: '/production/cut-order/page', method: 'get', params })
}
export function getCutOrder(id: number) {
  return request({ url: `/production/cut-order/${id}`, method: 'get' })
}
export function createCutOrder(data: any) {
  return request({ url: '/production/cut-order', method: 'post', data })
}
export function updateCutOrder(id: number, data: any) {
  return request({ url: `/production/cut-order/${id}`, method: 'put', data })
}
export function deleteCutOrder(id: number) {
  return request({ url: `/production/cut-order/${id}`, method: 'delete' })
}

// Cut Bundle API
export function getCutBundleList(params: any) {
  return request({ url: '/production/cut-bundle/page', method: 'get', params })
}
export function getCutBundle(id: number) {
  return request({ url: `/production/cut-bundle/${id}`, method: 'get' })
}
export function createCutBundle(data: any) {
  return request({ url: '/production/cut-bundle', method: 'post', data })
}
export function updateCutBundle(id: number, data: any) {
  return request({ url: `/production/cut-bundle/${id}`, method: 'put', data })
}
export function deleteCutBundle(id: number) {
  return request({ url: `/production/cut-bundle/${id}`, method: 'delete' })
}
