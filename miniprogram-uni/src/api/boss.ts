import http from '@/utils/request'

export function getDashboard() {
  return http.get('/boss/dashboard')
}

export function getKpi() {
  return http.get('/boss/kpi')
}

export function getProductionProgress() {
  return http.get('/boss/production/progress')
}

export function getAlerts(params: any) {
  return http.get('/boss/alert/list', { params })
}

export function getReports(params: any) {
  return http.get('/boss/report/list', { params })
}

export function getReportDetail(id: number) {
  return http.get(`/boss/report/${id}`)
}
