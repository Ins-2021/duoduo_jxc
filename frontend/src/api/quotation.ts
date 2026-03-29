import request from '@/utils/request'

export function getQuotationList(params: any) { return request({ url: '/api/quotation/list', method: 'get', params }) }
export function convertQuotation(id: number) { return request({ url: `/api/quotation/${id}/convert`, method: 'post' }) }
