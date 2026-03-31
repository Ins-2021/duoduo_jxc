import request from '@/utils/request'

export function getQuotationList(params: any) { return request({ url: '/quotation/list', method: 'get', params }) }
export function convertQuotation(id: number) { return request({ url: `/quotation/${id}/convert`, method: 'post' }) }
