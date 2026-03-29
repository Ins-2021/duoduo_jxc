import request from '@/utils/request'
import type { SalesOrderDTO, PageQuery } from '@/types'

export function getSalesOrderList(params: PageQuery & { docNo?: string; orderType?: number; status?: number; customerId?: number }) {
  return request({
    url: '/sales/page',
    method: 'get',
    params
  })
}

export function getSalesOrderDetailPage(params: PageQuery & { docNo?: string; customerId?: number; spuId?: number; orderType?: number }) {
  return request({
    url: '/sales/detail/page',
    method: 'get',
    params
  })
}

export function getSalesOrderDetail(id: number) {
  return request({
    url: `/sales/${id}`,
    method: 'get'
  })
}

export function createSalesOrder(data: SalesOrderDTO) {
  return request({
    url: '/sales',
    method: 'post',
    data
  })
}

export function updateSalesOrder(data: SalesOrderDTO) {
  return request({
    url: '/sales',
    method: 'put',
    data
  })
}

export function deleteSalesOrder(id: number) {
  return request({
    url: `/sales/${id}`,
    method: 'delete'
  })
}

export function auditSalesOrder(id: number) {
  return request({
    url: `/sales/${id}/audit`,
    method: 'post'
  })
}

export function unauditSalesOrder(id: number) {
  return request({
    url: `/sales/${id}/unaudit`,
    method: 'post'
  })
}

export function convertToSales(id: number) {
  return request({
    url: `/sales/${id}/convert`,
    method: 'post'
  })
}

export function getBookingUnfulfilled(bookingOrderId: number) {
  return request({
    url: `/sales/booking/${bookingOrderId}/unfulfilled`,
    method: 'get'
  })
}

export function partialDelivery(bookingOrderId: number, data: any) {
  return request({
    url: `/sales/booking/${bookingOrderId}/delivery`,
    method: 'post',
    data
  })
}
