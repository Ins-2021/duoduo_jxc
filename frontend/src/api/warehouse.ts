import request from '@/utils/request'
import type { WarehouseDTO, WarehouseQuery } from '@/types'

export function getWarehouseList(params: WarehouseQuery) {
  return request({
    url: '/warehouse/page',
    method: 'get',
    params
  })
}

export function getWarehouseDetail(id: number) {
  return request<WarehouseDTO>({
    url: `/warehouse/${id}`,
    method: 'get'
  })
}

export function addWarehouse(data: Omit<WarehouseDTO, 'warehouseId'>) {
  return request({
    url: '/warehouse',
    method: 'post',
    data
  })
}

export function updateWarehouse(data: WarehouseDTO) {
  return request({
    url: '/warehouse',
    method: 'put',
    data
  })
}

export function updateWarehouseStatus(id: number, status: number) {
  return request({
    url: `/warehouse/${id}/status`,
    method: 'put',
    data: { status }
  })
}
