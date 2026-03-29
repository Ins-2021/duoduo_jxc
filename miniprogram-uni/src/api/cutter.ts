import http from '@/utils/request'

export function getCuttingTasks(params: any) {
  return http.get('/cutter/task/list', { params })
}

export function getCuttingTaskDetail(id: number) {
  return http.get(`/cutter/task/${id}`)
}

export function submitCuttingInput(data: any) {
  return http.post('/cutter/input', data)
}

export function getPendingOrders() {
  return http.get('/cutter/order/pending')
}

export function generateBundles(data: any) {
  return http.post('/cutter/bundle/generate', data)
}

export function getBundleDetail(id: number) {
  return http.get(`/cutter/bundle/${id}`)
}

export function printBundleLabel(id: number) {
  return http.post(`/cutter/bundle/${id}/print`)
}

export function getTodayStats() {
  return http.get('/cutter/stats/today')
}

export function getTodayRecords() {
  return http.get('/cutter/records/today')
}
