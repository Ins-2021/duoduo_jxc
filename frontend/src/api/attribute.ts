import request from '@/utils/request'
import type { ProductAttributeDTO, ProductAttributeValueDTO } from '@/types'

export function getAttributeList() { return request({ url: '/product/attribute/list', method: 'get' }) }
export function addAttribute(data: ProductAttributeDTO) { return request({ url: '/product/attribute', method: 'post', data }) }
export function updateAttribute(data: ProductAttributeDTO) { return request({ url: '/product/attribute', method: 'put', data }) }
export function deleteAttribute(id: number) { return request({ url: `/product/attribute/${id}`, method: 'delete' }) }
export function addAttributeOption(attributeId: number, data: ProductAttributeValueDTO) { return request({ url: `/product/attribute/${attributeId}/value`, method: 'post', data }) }
export function updateAttributeOption(id: number, data: ProductAttributeValueDTO) { return request({ url: `/product/attribute/value/${id}`, method: 'put', data }) }
export function deleteAttributeOption(id: number) { return request({ url: `/product/attribute/value/${id}`, method: 'delete' }) }
export function batchSaveAttributeOptions(attributeId: number, data: ProductAttributeValueDTO[]) { return request({ url: `/product/attribute/${attributeId}/values/batch`, method: 'post', data }) }

export function getAttributeDetail(id: number) {
  return request({
    url: `/product/attributes/${id}`,
    method: 'get'
  })
}
