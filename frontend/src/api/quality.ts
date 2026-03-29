import request from '@/utils/request'

export function getQualityStandardList(params: any) { return request({ url: '/api/qualitystandard/list', method: 'get', params }) }
export function getQualityCheckList(params: any) { return request({ url: '/api/qualitycheck/list', method: 'get', params }) }
