import http from '@/utils/request'

export function scanWithQrCode(qrCodeNo: string, processId: number, quantity: number) {
  return http.post('/mes/scan/qrcode', null, {
    params: { qrCodeNo, processId, quantity }
  })
}

export function scanManual(data: { bundleNo: string; processId: number; workerId: number; quantity: number }) {
  return http.post('/mes/scan', data)
}

export function getTodayRecords(workerId: number) {
  return http.get(`/mes/scan/today/${workerId}`)
}

export function getScanRecords(params: any) {
  return http.get('/mes/scan/page', { params })
}

export function confirmRecord(recordId: number) {
  return http.post(`/mes/scan/${recordId}/confirm`)
}

export function rejectRecord(recordId: number, reason?: string) {
  return http.post(`/mes/scan/${recordId}/reject`, null, {
    params: { reason }
  })
}

export function getBundleFlowHistory(bundleId: number) {
  return http.get(`/mes/flow/history/${bundleId}`)
}

export function getQrCodeInfo(qrCodeNo: string) {
  return http.get(`/mes/qrcode/${qrCodeNo}`)
}
