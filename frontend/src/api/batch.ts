import request from '@/utils/request'

export interface BatchDTO {
  batchId?: number
  batchNo?: string
  skuId?: number
  warehouseId?: number
  productionDate?: string
  expiryDate?: string
  inboundDate?: string
  qty?: number
  remark?: string
}

export interface BatchQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  skuId?: number
  warehouseId?: number
  batchNo?: string
}

export function getBatchList(params: BatchQuery) {
  return request({
    url: '/inventory/batch/page',
    method: 'get',
    params
  })
}

export function getBatchDetail(id: number) {
  return request({
    url: `/inventory/batch/${id}`,
    method: 'get'
  })
}

export function addBatch(data: Omit<BatchDTO, 'batchId'>) {
  return request({
    url: '/inventory/batch',
    method: 'post',
    data
  })
}

export function updateBatch(id: number, data: BatchDTO) {
  return request({
    url: `/inventory/batch/${id}`,
    method: 'put',
    data
  })
}

export function deleteBatch(id: number) {
  return request({
    url: `/inventory/batch/${id}`,
    method: 'delete'
  })
}
