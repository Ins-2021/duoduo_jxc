import http from '@/utils/request'

export function getBundleInfo(bundleNo: string) {
  return http.get('/worker/bundle/info', { params: { bundleNo } })
}

export function getProcessList(processCode?: string) {
  return http.get('/worker/process/list', { params: { processCode } })
}

export function submitPiecework(data: any) {
  return http.post('/worker/piecework', data)
}

export function getTodayRecords() {
  return http.get('/worker/piecework/today')
}

export function getWageSummary(month: string) {
  return http.get('/worker/wage/summary', { params: { month } })
}

export function getWageDetail(month: string, params: any) {
  return http.get('/worker/wage/detail', { params: { month, ...params } })
}

export function getMyTasks(params: any) {
  return http.get('/worker/task/list', { params })
}
