import request from '@/utils/request'
import type { SupplierDTO, SupplierQuery } from '@/types'

export function getSupplierList(params: SupplierQuery) {
  return request({
    url: '/supplier/page',
    method: 'get',
    params
  })
}

export function getSupplierDetail(id: number) {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

export function addSupplier(data: Omit<SupplierDTO, 'supplierId'>) {
  return request({
    url: '/supplier',
    method: 'post',
    data
  })
}

export function updateSupplier(data: SupplierDTO) {
  return request({
    url: '/supplier',
    method: 'put',
    data
  })
}

export function updateSupplierStatus(id: number, status: number) {
  return request({
    url: `/supplier/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function getSupplierOptions() {
  return request({ url: '/supplier/options', method: 'get' })
}
