import request from '@/utils/request'

export interface StoreDTO {
  storeId: number
  storeCode: string
  storeName: string
  address: string
  phone: string
  contactName: string
  status: number
  remark: string
}

export interface StoreQuery {
  pageNum: number
  pageSize: number
  storeCode?: string
  storeName?: string
  status?: number
}

export function getStorePage(params: StoreQuery) {
  return request({
    url: '/store/page',
    method: 'get',
    params
  })
}

export function getStoreById(id: number) {
  return request({
    url: `/store/${id}`,
    method: 'get'
  })
}

export function getStoreList() {
  return request({
    url: '/store/list',
    method: 'get'
  })
}

export function createStore(data: StoreDTO) {
  return request({
    url: '/store',
    method: 'post',
    data
  })
}

export function updateStore(data: StoreDTO) {
  return request({
    url: '/store',
    method: 'put',
    data
  })
}

export function deleteStore(id: number) {
  return request({
    url: `/store/${id}`,
    method: 'delete'
  })
}
