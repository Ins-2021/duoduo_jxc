import request from '@/utils/request'

export function getQualityStandardList(params: any) { return request({ url: '/qualitystandard/list', method: 'get', params }) }
export function getQualityCheckList(params: any) { return request({ url: '/quality/checks', method: 'get', params }) }
