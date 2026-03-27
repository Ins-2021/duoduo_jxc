import request from '@/utils/request'
import type { SalesReturnOrderDTO, SalesReturnSourceOrderDTO, PageQuery } from '@/types'

export function getSalesReturnOrderList(params: PageQuery & { docNo?: string; customerId?: number; status?: number }) {
  return request({
    url: '/sales-return/page',
    method: 'get',
    params
  })
}

export function getSalesReturnOrderDetail(id: number) {
  return request<SalesReturnOrderDTO>({
    url: `/sales-return/${id}`,
    method: 'get'
  })
}

export function getSalesReturnSourceOrder(id: number) {
  return request<SalesReturnSourceOrderDTO>({
    url: `/sales-return/source/${id}`,
    method: 'get'
  })
}

export function createSalesReturnOrder(data: SalesReturnOrderDTO) {
  return request({
    url: '/sales-return',
    method: 'post',
    data
  })
}

export function updateSalesReturnOrder(data: SalesReturnOrderDTO) {
  return request({
    url: '/sales-return',
    method: 'put',
    data
  })
}

export function deleteSalesReturnOrder(id: number) {
  return request({
    url: `/sales-return/${id}`,
    method: 'delete'
  })
}

export function auditSalesReturnOrder(id: number) {
  return request({
    url: `/sales-return/${id}/audit`,
    method: 'post'
  })
}
