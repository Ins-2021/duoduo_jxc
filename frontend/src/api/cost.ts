import request from '@/utils/request'

// 成本中心
export function getCostCenterPage(params: any) { return request({ url: '/cost/center/page', method: 'get', params }) }
export function getCostCenterDetail(id: number) { return request({ url: `/cost/center/${id}`, method: 'get' }) }
export function createCostCenter(data: any) { return request({ url: '/cost/center', method: 'post', data }) }
export function updateCostCenter(id: number, data: any) { return request({ url: `/cost/center/${id}`, method: 'put', data }) }
export function deleteCostCenter(id: number) { return request({ url: `/cost/center/${id}`, method: 'delete' }) }

// 成本池
export function getCostPoolPage(params: any) { return request({ url: '/cost/pool/page', method: 'get', params }) }
export function getCostPoolDetail(id: number) { return request({ url: `/cost/pool/${id}`, method: 'get' }) }
export function createCostPool(data: any) { return request({ url: '/cost/pool', method: 'post', data }) }
export function updateCostPool(id: number, data: any) { return request({ url: `/cost/pool/${id}`, method: 'put', data }) }
export function deleteCostPool(id: number) { return request({ url: `/cost/pool/${id}`, method: 'delete' }) }

// 成本分摊
export function getCostAllocationPage(params: any) { return request({ url: '/cost/allocation/page', method: 'get', params }) }
export function getCostAllocationDetail(id: number) { return request({ url: `/cost/allocation/${id}`, method: 'get' }) }
export function createCostAllocation(data: any) { return request({ url: '/cost/allocation', method: 'post', data }) }
export function updateCostAllocation(id: number, data: any) { return request({ url: `/cost/allocation/${id}`, method: 'put', data }) }
export function deleteCostAllocation(id: number) { return request({ url: `/cost/allocation/${id}`, method: 'delete' }) }

// 产品成本
export function getProductCostPage(params: any) { return request({ url: '/cost/product/page', method: 'get', params }) }
export function getProductCostDetail(id: number) { return request({ url: `/cost/product/${id}`, method: 'get' }) }
export function createProductCost(data: any) { return request({ url: '/cost/product', method: 'post', data }) }
export function updateProductCost(id: number, data: any) { return request({ url: `/cost/product/${id}`, method: 'put', data }) }
export function deleteProductCost(id: number) { return request({ url: `/cost/product/${id}`, method: 'delete' }) }

// 成本核算期间
export function getCostPeriodPage(params: any) { return request({ url: '/cost/period/page', method: 'get', params }) }
export function getCostPeriodDetail(id: number) { return request({ url: `/cost/period/${id}`, method: 'get' }) }
export function createCostPeriod(data: any) { return request({ url: '/cost/period', method: 'post', data }) }
export function updateCostPeriod(id: number, data: any) { return request({ url: `/cost/period/${id}`, method: 'put', data }) }
export function deleteCostPeriod(id: number) { return request({ url: `/cost/period/${id}`, method: 'delete' }) }
export function updateCostPeriodStatus(id: number, status: number) { return request({ url: `/cost/period/${id}/status`, method: 'put', params: { status } }) }

// 成本汇总
export function getCostSummaryPage(params: any) { return request({ url: '/cost/summary/page', method: 'get', params }) }
export function getCostSummaryDetail(id: number) { return request({ url: `/cost/summary/${id}`, method: 'get' }) }

// 成本差异分析
export function getCostVariancePage(params: any) { return request({ url: '/cost/variance/page', method: 'get', params }) }
export function analyzeCostVariance(id: number) { return request({ url: `/cost/variance/${id}/analyze`, method: 'put' }) }

// 标准成本
export function getStandardCostPage(params: any) { return request({ url: '/cost/standard/page', method: 'get', params }) }
export function getStandardCostDetail(id: number) { return request({ url: `/cost/standard/${id}`, method: 'get' }) }
export function createStandardCost(data: any) { return request({ url: '/cost/standard', method: 'post', data }) }
export function updateStandardCost(id: number, data: any) { return request({ url: `/cost/standard/${id}`, method: 'put', data }) }
export function deleteStandardCost(id: number) { return request({ url: `/cost/standard/${id}`, method: 'delete' }) }
