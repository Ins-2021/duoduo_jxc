import http from '@/utils/request'

export function getOverview() {
  return http.get('/leader/overview')
}

export function getPendingBundles() {
  return http.get('/leader/bundle/pending')
}

export function getWorkerList() {
  return http.get('/leader/worker/list')
}

export function assignTask(data: any) {
  return http.post('/leader/assign', data)
}

export function getProgressList(params: any) {
  return http.get('/leader/progress/list', { params })
}

export function getExceptionList(params: any) {
  return http.get('/leader/exception/list', { params })
}

export function handleException(data: any) {
  return http.post('/leader/exception/handle', data)
}

export function getApprovalList(params: any) {
  return http.get('/leader/approval/list', { params })
}

export function approve(data: any) {
  return http.post('/leader/approval', data)
}
