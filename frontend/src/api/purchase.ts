import request from '@/utils/request'
import type { PurchaseOrderDTO, PageQuery } from '@/types'

export function getPurchaseOrderList(params: PageQuery & { docNo?: string; orderType?: number; status?: number; supplierId?: number }) {
  return request({
    url: '/purchase/page',
    method: 'get',
    params
  })
}

export function getPurchaseOrderDetailPage(params: PageQuery & { docNo?: string; supplierId?: number; spuId?: number; orderType?: number }) {
  return request({
    url: '/purchase/detail/page',
    method: 'get',
    params
  })
}

export function getPurchaseOrderDetail(id: number) {
  return request<PurchaseOrderDTO>({
    url: `/purchase/${id}`,
    method: 'get'
  })
}

export function createPurchaseOrder(data: PurchaseOrderDTO) {
  return request({
    url: '/purchase',
    method: 'post',
    data
  })
}

export function updatePurchaseOrder(data: PurchaseOrderDTO) {
  return request({
    url: '/purchase',
    method: 'put',
    data
  })
}

export function deletePurchaseOrder(id: number) {
  return request({
    url: `/purchase/${id}`,
    method: 'delete'
  })
}
