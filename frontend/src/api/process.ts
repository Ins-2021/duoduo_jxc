import request from '@/utils/request'

export function getProcessList(params: any) { return request({ url: '/process/list', method: 'get', params }) }
export function getProcessDetail(id: number) { return request({ url: `/process/${id}`, method: 'get' }) }
export function createProcess(data: any) { return request({ url: '/process', method: 'post', data }) }
export function updateProcess(id: number, data: any) { return request({ url: `/process/${id}`, method: 'put', data }) }
export function deleteProcess(id: number) { return request({ url: `/process/${id}`, method: 'delete' }) }
