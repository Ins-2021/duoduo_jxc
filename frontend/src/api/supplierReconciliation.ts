import request from '@/utils/request'

export interface SupplierReconciliationDTO {
  reconciliationId?: number
  reconNo?: string
  supplierId?: number
  supplierName?: string
  startDate?: string
  endDate?: string
  totalAmount?: number
  paidAmount?: number
  unpaidAmount?: number
  status?: number
  remark?: string
}

export interface SupplierReconciliationQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  supplierId?: number
  status?: number
  startDate?: string
  endDate?: string
}

export function getReconList(params: SupplierReconciliationQuery) {
  return request({
    url: '/supplier/reconciliation/page',
    method: 'get',
    params
  })
}

export function getReconDetail(id: number) {
  return request({
    url: `/supplier/reconciliation/${id}`,
    method: 'get'
  })
}

export function addRecon(data: Omit<SupplierReconciliationDTO, 'reconciliationId'>) {
  return request({
    url: '/supplier/reconciliation',
    method: 'post',
    data
  })
}

export function updateRecon(id: number, data: SupplierReconciliationDTO) {
  return request({
    url: `/supplier/reconciliation/${id}`,
    method: 'put',
    data
  })
}

export function confirmRecon(id: number) {
  return request({
    url: `/supplier/reconciliation/${id}/confirm`,
    method: 'post'
  })
}

export function deleteRecon(id: number) {
  return request({
    url: `/supplier/reconciliation/${id}`,
    method: 'delete'
  })
}
