import request from '@/utils/request'
import type { CustomerDTO, CustomerQuery } from '@/types'

export function getCustomerList(params: CustomerQuery) {
  return request({
    url: '/customer/page',
    method: 'get',
    params
  })
}

export function getCustomerDetail(id: number) {
  return request({
    url: `/customer/${id}`,
    method: 'get'
  })
}

export function addCustomer(data: Omit<CustomerDTO, 'customerId'>) {
  return request({
    url: '/customer',
    method: 'post',
    data
  })
}

export function updateCustomer(data: CustomerDTO) {
  return request({
    url: '/customer',
    method: 'put',
    data
  })
}

export function updateCustomerStatus(id: number, status: number) {
  return request({
    url: `/customer/${id}/status`,
    method: 'put',
    data: { status }
  })
}
