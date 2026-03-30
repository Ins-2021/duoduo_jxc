import request from '@/utils/request'

export function getBundleList(params: any) {
  return request({ url: '/api/bundle/list', method: 'get', params })
}

export function getBundle(id: number) {
  return request({ url: `/api/bundle/${id}`, method: 'get' })
}

export function createBundle(data: any) {
  return request({ url: '/api/bundle', method: 'post', data })
}

export function updateBundle(id: number, data: any) {
  return request({ url: `/api/bundle/${id}`, method: 'put', data })
}

export function deleteBundle(id: number) {
  return request({ url: `/api/bundle/${id}`, method: 'delete' })
}

export function getPatrolCheckList(params: any) {
  return request({ url: '/api/quality/patrols', method: 'get', params })
}

export function getPatrolCheck(id: number) {
  return request({ url: `/api/quality/patrols/${id}`, method: 'get' })
}

export function createPatrolCheck(data: any) {
  return request({ url: '/api/quality/patrols', method: 'post', data })
}

export function updatePatrolCheck(id: number, data: any) {
  return request({ url: `/api/quality/patrols/${id}`, method: 'put', data })
}

export function deletePatrolCheck(id: number) {
  return request({ url: `/api/quality/patrols/${id}`, method: 'delete' })
}

export function getAqlList(params: any) {
  return request({ url: '/api/quality/aql', method: 'get', params })
}

export function getAql(id: number) {
  return request({ url: `/api/quality/aql/${id}`, method: 'get' })
}

export function createAql(data: any) {
  return request({ url: '/api/quality/aql', method: 'post', data })
}

export function updateAql(id: number, data: any) {
  return request({ url: `/api/quality/aql/${id}`, method: 'put', data })
}

export function deleteAql(id: number) {
  return request({ url: `/api/quality/aql/${id}`, method: 'delete' })
}

export function calculateAql(batchSize: number, level: string) {
  return request({ 
    url: '/api/quality/aql/calculate', 
    method: 'get', 
    params: { batchSize, level } 
  })
}

export function getReworkList(params: any) {
  return request({ url: '/api/quality/reworks', method: 'get', params })
}

export function getRework(id: number) {
  return request({ url: `/api/quality/reworks/${id}`, method: 'get' })
}

export function getReworkByCheckId(checkId: number) {
  return request({ url: `/api/quality/reworks/by-check/${checkId}`, method: 'get' })
}

export function createRework(data: any) {
  return request({ url: '/api/quality/reworks', method: 'post', data })
}

export function updateRework(id: number, data: any) {
  return request({ url: `/api/quality/reworks/${id}`, method: 'put', data })
}

export function deleteRework(id: number) {
  return request({ url: `/api/quality/reworks/${id}`, method: 'delete' })
}

export function getPieceRecordList(params: any) {
  return request({ url: '/wage/piece/page', method: 'get', params })
}

export function getPieceRecord(id: number) {
  return request({ url: `/wage/piece/${id}`, method: 'get' })
}

export function createPieceRecord(data: any) {
  return request({ url: '/wage/piece', method: 'post', data })
}

export function updatePieceRecord(id: number, data: any) {
  return request({ url: `/wage/piece/${id}`, method: 'put', data })
}

export function deletePieceRecord(id: number) {
  return request({ url: `/wage/piece/${id}`, method: 'delete' })
}

export function auditPieceRecord(id: number, auditStatus: number) {
  return request({ 
    url: `/wage/piece/${id}/audit`, 
    method: 'put', 
    params: { auditStatus } 
  })
}

export function getPieceRecordSummary(params: any) {
  return request({ url: '/wage/piece/summary', method: 'get', params })
}
