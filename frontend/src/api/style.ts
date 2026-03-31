import request from '@/utils/request'

export function getStyleList(params: any) {
  return request({ url: '/style/list', method: 'get', params })
}
export function getStyleDetail(id: number) {
  return request({ url: `/style/${id}`, method: 'get' })
}
export function createStyle(data: any) {
  return request({ url: '/style', method: 'post', data })
}
export function updateStyle(id: number, data: any) {
  return request({ url: `/style/${id}`, method: 'put', data })
}
export function deleteStyle(id: number) {
  return request({ url: `/style/${id}`, method: 'delete' })
}
