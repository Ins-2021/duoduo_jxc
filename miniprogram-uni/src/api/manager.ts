import http from '@/utils/request'

export function getDashboard() {
  return http.get('/manager/dashboard')
}

export function getOrders(params: any) {
  return http.get('/manager/order/list', { params })
}

export function getOrderDetail(id: number) {
  return http.get(`/manager/order/${id}`)
}

export function getStaffList(params: any) {
  return http.get('/manager/staff/list', { params })
}

export function getReports(params: any) {
  return http.get('/manager/report/list', { params })
}

export function getReportDetail(id: number) {
  return http.get(`/manager/report/${id}`)
}
