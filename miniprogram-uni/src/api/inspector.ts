import http from '@/utils/request'

export function getBundleInfo(bundleNo: string) {
  return http.get('/inspector/bundle/info', { params: { bundleNo } })
}

export function submitQualityCheck(data: any) {
  return http.post('/inspector/check', data)
}

export function getTodayStats() {
  return http.get('/inspector/stats/today')
}

export function getPendingFirstArticles() {
  return http.get('/inspector/first-article/pending')
}

export function confirmFirstArticle(id: number, data: any) {
  return http.post(`/inspector/first-article/${id}/confirm`, data)
}

export function getReworkList(params: any) {
  return http.get('/inspector/rework/list', { params })
}

export function submitRework(data: any) {
  return http.post('/inspector/rework', data)
}

export function getCheckHistory(params: any) {
  return http.get('/inspector/history', { params })
}
