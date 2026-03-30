import request from '@/utils/request'

export function getFabricList(params: any) {
  return request({ url: '/api/fabrics/page', method: 'get', params })
}

export function getFabric(id: number) {
  return request({ url: `/api/fabrics/${id}`, method: 'get' })
}

export function createFabric(data: any) {
  return request({ url: '/api/fabrics', method: 'post', data })
}

export function updateFabric(id: number, data: any) {
  return request({ url: `/api/fabrics/${id}`, method: 'put', data })
}

export function deleteFabric(id: number) {
  return request({ url: `/api/fabrics/${id}`, method: 'delete' })
}

export function getFabricAll() {
  return request({ url: '/api/fabrics/list', method: 'get' })
}

export function getFabricInboundList(params: any) {
  return request({ url: '/api/fabrics/inbounds/page', method: 'get', params })
}

export function getFabricInbound(id: number) {
  return request({ url: `/api/fabrics/inbounds/${id}`, method: 'get' })
}

export function createFabricInbound(data: any) {
  return request({ url: '/api/fabrics/inbounds', method: 'post', data })
}

export function approveFabricInbound(id: number) {
  return request({ url: `/api/fabrics/inbounds/${id}/approve`, method: 'post' })
}

export function deleteFabricInbound(id: number) {
  return request({ url: `/api/fabrics/inbounds/${id}`, method: 'delete' })
}

export function getFabricRequisitionList(params: any) {
  return request({ url: '/api/fabrics/requisitions/page', method: 'get', params })
}

export function getFabricRequisition(id: number) {
  return request({ url: `/api/fabrics/requisitions/${id}`, method: 'get' })
}

export function createFabricRequisition(data: any) {
  return request({ url: '/api/fabrics/requisitions', method: 'post', data })
}

export function approveFabricRequisition(id: number, comment: string, approved: boolean) {
  return request({ 
    url: `/api/fabrics/requisitions/${id}/approve`, 
    method: 'post', 
    params: { comment, approved } 
  })
}

export function issueFabricRequisition(id: number) {
  return request({ url: `/api/fabrics/requisitions/${id}/issue`, method: 'post' })
}

export function deleteFabricRequisition(id: number) {
  return request({ url: `/api/fabrics/requisitions/${id}`, method: 'delete' })
}

export function getFabricInventoryList(params: any) {
  return request({ url: '/api/fabrics/inventory/page', method: 'get', params })
}

export function getFabricInventoryByFabricId(fabricId: number) {
  return request({ url: `/api/fabrics/inventory/${fabricId}`, method: 'get' })
}

export function lockFabricStock(warehouseId: number, fabricId: number, quantity: number) {
  return request({ 
    url: '/api/fabrics/inventory/lock', 
    method: 'post', 
    params: { warehouseId, fabricId, quantity } 
  })
}

export function unlockFabricStock(warehouseId: number, fabricId: number, quantity: number) {
  return request({ 
    url: '/api/fabrics/inventory/unlock', 
    method: 'post', 
    params: { warehouseId, fabricId, quantity } 
  })
}

export function getFabricAlerts() {
  return request({ url: '/api/fabrics/inventory/alerts', method: 'get' })
}
