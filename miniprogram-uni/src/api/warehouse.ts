import http from '@/utils/request'

export function getOverview() {
  return http.get('/warehouse/overview')
}

export function inbound(data: any) {
  return http.post('/warehouse/in', data)
}

export function outbound(data: any) {
  return http.post('/warehouse/out', data)
}

export function getStockList(params: any) {
  return http.get('/warehouse/stock/list', { params })
}

export function getInventoryTasks(params: any) {
  return http.get('/warehouse/inventory/list', { params })
}

export function submitInventory(data: any) {
  return http.post('/warehouse/inventory', data)
}

export function getTodayRecords(type: string) {
  return http.get('/warehouse/records/today', { params: { type } })
}

export function getPendingList() {
  return http.get('/warehouse/pending')
}
