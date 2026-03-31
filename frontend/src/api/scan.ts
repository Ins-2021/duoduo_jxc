import request from '@/utils/request'

export function generateBundleQrCode(bundleId: number, bundleNo: string) {
  return request({
    url: `/mes/qrcode/bundle/${bundleId}`,
    method: 'post',
    params: { bundleNo }
  })
}

export function getQrCode(qrCodeNo: string) {
  return request({
    url: `/mes/qrcode/${qrCodeNo}`,
    method: 'get'
  })
}

export function verifyQrCode(qrCodeNo: string, signature: string) {
  return request({
    url: '/mes/qrcode/verify',
    method: 'post',
    params: { qrCodeNo, signature }
  })
}

export function scan(data: any) {
  return request({
    url: '/mes/scan',
    method: 'post',
    data
  })
}

export function scanWithQrCode(qrCodeNo: string, workerId: number, processId: number, quantity?: number) {
  return request({
    url: '/mes/scan/qrcode',
    method: 'post',
    params: { qrCodeNo, workerId, processId, quantity }
  })
}

export function getScanRecords(params: any) {
  return request({
    url: '/mes/scan/page',
    method: 'get',
    params
  })
}

export function getTodayRecords(workerId: number) {
  return request({
    url: `/mes/scan/today/${workerId}`,
    method: 'get'
  })
}

export function getScanDetail(recordId: number) {
  return request({
    url: `/mes/scan/${recordId}`,
    method: 'get'
  })
}

export function confirmRecord(recordId: number) {
  return request({
    url: `/mes/scan/${recordId}/confirm`,
    method: 'post'
  })
}

export function rejectRecord(recordId: number, reason?: string) {
  return request({
    url: `/mes/scan/${recordId}/reject`,
    method: 'post',
    params: { reason }
  })
}

export function transferBundle(data: any) {
  return request({
    url: '/mes/flow/transfer',
    method: 'post',
    data
  })
}

export function getBundleFlowHistory(bundleId: number) {
  return request({
    url: `/mes/flow/history/${bundleId}`,
    method: 'get'
  })
}

export function getLatestFlow(bundleId: number) {
  return request({
    url: `/mes/flow/latest/${bundleId}`,
    method: 'get'
  })
}
