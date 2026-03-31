import request from '@/utils/request'

export function getWorkGroupList(params: any) {
  return request({ url: '/work-group/page', method: 'get', params })
}

export function getWorkGroup(id: number) {
  return request({ url: `/work-group/${id}`, method: 'get' })
}

export function createWorkGroup(data: any) {
  return request({ url: '/work-group', method: 'post', data })
}

export function updateWorkGroup(id: number, data: any) {
  return request({ url: `/work-group/${id}`, method: 'put', data })
}

export function deleteWorkGroup(id: number) {
  return request({ url: `/work-group/${id}`, method: 'delete' })
}