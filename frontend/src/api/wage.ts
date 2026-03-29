import request from '@/utils/request'

// 计件记录
export function getPieceRecordPage(params: any) { return request({ url: '/wage/piece/page', method: 'get', params }) }
export function getPieceRecordDetail(id: number) { return request({ url: `/wage/piece/${id}`, method: 'get' }) }
export function createPieceRecord(data: any) { return request({ url: '/wage/piece', method: 'post', data }) }
export function updatePieceRecord(id: number, data: any) { return request({ url: `/wage/piece/${id}`, method: 'put', data }) }
export function deletePieceRecord(id: number) { return request({ url: `/wage/piece/${id}`, method: 'delete' }) }
export function auditPieceRecord(id: number, auditStatus: number) { return request({ url: `/wage/piece/${id}/audit`, method: 'put', params: { auditStatus } }) }
export function getPieceRecordSummary(params: any) { return request({ url: '/wage/piece/summary', method: 'get', params }) }

// 计件工价
export function getPiecePricePage(params: any) { return request({ url: '/wage/price/page', method: 'get', params }) }
export function getPiecePriceDetail(id: number) { return request({ url: `/wage/price/${id}`, method: 'get' }) }
export function createPiecePrice(data: any) { return request({ url: '/wage/price', method: 'post', data }) }
export function updatePiecePrice(id: number, data: any) { return request({ url: `/wage/price/${id}`, method: 'put', data }) }
export function deletePiecePrice(id: number) { return request({ url: `/wage/price/${id}`, method: 'delete' }) }

// 工资单
export function getPayrollSheetPage(params: any) { return request({ url: '/wage/sheet/page', method: 'get', params }) }
export function getPayrollSheetDetail(id: number) { return request({ url: `/wage/sheet/${id}`, method: 'get' }) }
export function createPayrollSheet(data: any) { return request({ url: '/wage/sheet', method: 'post', data }) }
export function updatePayrollSheet(id: number, data: any) { return request({ url: `/wage/sheet/${id}`, method: 'put', data }) }
export function deletePayrollSheet(id: number) { return request({ url: `/wage/sheet/${id}`, method: 'delete' }) }
export function updatePayrollSheetStatus(id: number, status: number) { return request({ url: `/wage/sheet/${id}/status`, method: 'put', params: { status } }) }
export function submitPayrollSheet(id: number) { return request({ url: `/wage/sheet/${id}/submit`, method: 'post' }) }
export function auditPayrollSheet(id: number, approved: number) { return request({ url: `/wage/sheet/${id}/audit`, method: 'post', params: { approved } }) }

// 工资单明细
export function getPayrollSheetDetailPage(params: any) { return request({ url: '/wage/sheet-detail/page', method: 'get', params }) }
export function getPayrollSheetDetailById(id: number) { return request({ url: `/wage/sheet-detail/${id}`, method: 'get' }) }

// 工资期间
export function getPayrollPeriodPage(params: any) { return request({ url: '/wage/period/page', method: 'get', params }) }
export function getPayrollPeriodDetail(id: number) { return request({ url: `/wage/period/${id}`, method: 'get' }) }
export function createPayrollPeriod(data: any) { return request({ url: '/wage/period', method: 'post', data }) }
export function updatePayrollPeriod(id: number, data: any) { return request({ url: `/wage/period/${id}`, method: 'put', data }) }
export function deletePayrollPeriod(id: number) { return request({ url: `/wage/period/${id}`, method: 'delete' }) }
export function updatePayrollPeriodStatus(id: number, status: number) { return request({ url: `/wage/period/${id}/status`, method: 'put', params: { status } }) }
export function submitPayrollPeriod(id: number) { return request({ url: `/wage/period/${id}/submit`, method: 'post' }) }
export function auditPayrollPeriod(id: number, approved: boolean) { return request({ url: `/wage/period/${id}/audit`, method: 'post', params: { approved } }) }
